def compute_recovery(snapshot, valuation, ltv_info, tenant_info, risk):

    checks = risk.get("checks") or {}
    level = risk.get("risk_level") or "LOW"

    deposit = tenant_info.get("deposit")
    move_in = tenant_info.get("move_in_date")
    fixed = tenant_info.get("fixed_date")

    property_value = valuation.get("median_price_won")

    # -------------------------
    # 선순위 채권 계산 (근저당)
    # -------------------------
    senior_claim = 0
    eulgu = snapshot.get("eulgu") or []

    for r in eulgu:
        if r.get("status") == "유효":
            amt = r.get("max_claim_amount")
            if amt:
                senior_claim += amt

    # -------------------------
    # 예상 경매가 (보수적 80%)
    # -------------------------
    if property_value:
        auction_value = int(property_value * 0.8)
    else:
        auction_value = None

    recoverable = None
    loss = None

    if auction_value:
        remain = auction_value - senior_claim

        if remain <= 0:
            recoverable = 0
        else:
            recoverable = min(remain, deposit)

        loss = deposit - recoverable

    # -------------------------
    # playbook 생성
    # -------------------------
    playbook = []
    step = 1

    if checks.get("has_active_auction"):
        playbook.append({
            "step": step,
            "title": "경매 진행 여부 확인",
            "how": [
                "법원 경매 사건번호 확인",
                "점유 및 임대차 관계 조사"
            ],
            "output": "경매 상태 요약"
        })
        step += 1

    if checks.get("has_seizure"):
        playbook.append({
            "step": step,
            "title": "압류/가압류 확인",
            "how": [
                "채권자 확인",
                "해제 가능 여부 확인"
            ],
            "output": "압류 대응 메모"
        })
        step += 1

    if checks.get("has_active_mortgage"):
        playbook.append({
            "step": step,
            "title": "근저당 채무 확인",
            "how": [
                "채권최고액 vs 실제 채무액 확인",
                "말소 조건 확인"
            ],
            "output": "채무잔액 확인"
        })
        step += 1

    priority = "IMMEDIATE" if level == "HIGH" else ("SOON" if level == "MEDIUM" else "NORMAL")

    return {
        "priority": priority,

        "tenant_input": {
            "deposit": deposit,
            "move_in_date": move_in,
            "fixed_date": fixed
        },

        "calculation": {
            "property_value": property_value,
            "auction_estimate": auction_value,
            "senior_claim_total": senior_claim,
            "recoverable_amount": recoverable,
            "loss_amount": loss
        },

        "playbook": playbook
    }