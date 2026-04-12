WEIGHTS = {
    "근저당권설정": 30,
    "저당권설정": 30,
    "전세권설정": 18,
    "임차권등기": 18,
    "압류": 35,
    "가압류": 35,
    "경매": 60,
    "가처분": 45
}


def extract_signals_from_snapshot(snapshot: dict):
    eulgu = snapshot.get("eulgu") or []
    active = [e for e in eulgu if (e.get("status") or "유효") != "말소"]

    flags = {k: False for k in WEIGHTS.keys()}
    evidence = {k: [] for k in WEIGHTS.keys()}

    for e in active:
        p = e.get("purpose")
        if p in flags:
            flags[p] = True
            evidence[p].append({
                "rank": e.get("rank"),
                "purpose": p,
                "max_claim_amount": e.get("max_claim_amount"),
                "status": e.get("status") or "유효"
            })

    return {
        "flags": flags,
        "evidence": evidence,
        "meta": {
            "eulgu_total": len(eulgu),
            "eulgu_active": len(active),
            "gabu_total": len(snapshot.get("gabu") or [])
        }
    }


def compute_ltv_info(snapshot: dict, valuation: dict):
    price = valuation.get("median_price_won") if valuation else None
    if not price or not isinstance(price, int) or price <= 0:
        return {
            "ok": False,
            "reason": "NO_PRICE",
            "house_price_won": None
        }

    eulgu = snapshot.get("eulgu") or []
    active = [e for e in eulgu if (e.get("status") or "유효") != "말소"]
    max_claim_total = sum(
        e.get("max_claim_amount")
        for e in active
        if isinstance(e.get("max_claim_amount"), int)
    )

    ltv = max_claim_total / price if price > 0 else None

    return {
        "ok": True,
        "reason": None,
        "house_price_won": int(price),
        "max_claim_total_won": int(max_claim_total),
        "ltv": round(float(ltv), 4) if isinstance(ltv, (int, float)) else None,
        "method": "ltv = max_claim_total / house_price"
    }


def compute_risk(snapshot: dict, diff: dict, valuation: dict, ltv_info: dict):
    score = 0
    signals_out = []

    sig = extract_signals_from_snapshot(snapshot)
    flags = sig.get("flags") or {}
    evidence_map = sig.get("evidence") or {}

    for p, w in WEIGHTS.items():
        if flags.get(p):
            score += w
            evid_rows = evidence_map.get(p) or []
            rep = evid_rows[0] if evid_rows else {"purpose": p}
            signals_out.append({
                "code": f"EULGU_ACTIVE_{p}",
                "severity": "HIGH" if w >= 35 else "MEDIUM",
                "evidence": {
                    "representative": rep,
                    "rows": evid_rows
                },
                "explain": f"을구에 유효한 '{p}' 항목이 있어 위험도가 상승합니다."
            })

    if diff and diff.get("baseline_present"):
        added = (diff.get("changes", {}).get("eulgu", {}).get("added") or [])
        for e in added:
            p = e.get("purpose") or "UNKNOWN"
            score += 15
            signals_out.append({
                "code": "DIFF_EULGU_ADDED",
                "severity": "HIGH",
                "evidence": {
                    "rank": e.get("rank"),
                    "purpose": p,
                    "max_claim_amount": e.get("max_claim_amount")
                },
                "explain": "이전 대비 을구 항목이 새로 추가되었습니다(신규 위험 신호)."
            })

    total_amt = int((ltv_info or {}).get("max_claim_total_won") or 0)

    if total_amt >= 300_000_000:
        score += 10
        signals_out.append({
            "code": "EULGU_MAX_CLAIM_HIGH",
            "severity": "HIGH",
            "evidence": {"max_claim_amount_total": total_amt},
            "explain": "채권최고액 합계가 큰 편이라 보수적으로 위험을 높게 평가합니다."
        })
    elif total_amt >= 100_000_000:
        score += 5
        signals_out.append({
            "code": "EULGU_MAX_CLAIM_MED",
            "severity": "MEDIUM",
            "evidence": {"max_claim_amount_total": total_amt},
            "explain": "채권최고액 합계가 존재하여 추가 확인이 필요합니다."
        })

    if ltv_info and ltv_info.get("ok") and ltv_info.get("house_price_won"):
        ltv = ltv_info.get("ltv")
        conf = (valuation or {}).get("confidence", "NONE")

        if isinstance(ltv, float):
            if ltv >= 0.85:
                score += 25
                sev = "HIGH"
                msg = "LTV가 85% 이상으로 매우 높습니다."
            elif ltv >= 0.70:
                score += 15
                sev = "HIGH"
                msg = "LTV가 70% 이상으로 높습니다."
            elif ltv >= 0.50:
                score += 8
                sev = "MEDIUM"
                msg = "LTV가 50% 이상으로 추가 확인이 필요합니다."
            else:
                sev = "LOW"
                msg = "LTV가 낮은 편입니다."

            signals_out.append({
                "code": "LTV_ESTIMATED",
                "severity": sev,
                "evidence": {
                    "ltv": ltv,
                    "house_price_won": ltv_info.get("house_price_won"),
                    "max_claim_total_won": ltv_info.get("max_claim_total_won"),
                    "valuation_confidence": conf,
                    "valuation_sample_count": (valuation or {}).get("sample_count")
                },
                "explain": msg + " (실거래가 중앙값 기반 추정)"
            })
    else:
        signals_out.append({
            "code": "LTV_UNAVAILABLE",
            "severity": "MEDIUM",
            "evidence": {"reason": (ltv_info or {}).get("reason")},
            "explain": "시세 추정이 불가하여 LTV 기반 판단을 생략했습니다."
        })

    score = max(0, min(100, score))
    level = "HIGH" if score >= 70 else ("MEDIUM" if score >= 40 else "LOW")

    checks = {
        "has_active_mortgage": bool(flags.get("근저당권설정") or flags.get("저당권설정")),
        "has_seizure": bool(flags.get("압류") or flags.get("가압류")),
        "has_active_auction": bool(flags.get("경매")),
        "max_claim_amount_total": total_amt,
        "ltv": (ltv_info or {}).get("ltv"),
        "valuation_confidence": (valuation or {}).get("confidence", "NONE"),
        "signals_meta": (sig.get("meta") or {})
    }

    return {
        "risk_score": score,
        "risk_level": level,
        "signals": signals_out,
        "checks": checks
    }