package com.capstone.houseviewingapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.capstone.houseviewingapp.home.HouseCardItem
import org.json.JSONArray
import org.json.JSONObject

 // TODO : 백엔드 연동 후 Repository(Remote + Room 등)로 교체 예정

object HouseLocalStore {
    // NOTE : SharedPreferences에 집 목록을 JSON 문자열로 저장하는 간단한 로컬 저장소 구현
    private const val PREF_NAME = "house_local_pref"
    private const val KEY_HOUSES_JSON = "houses_json"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // NOTE : 저장된  집 목록 반환 (JSON 문자열로) -> 없으면 빈 리스트
    fun getHouses(context: Context): List<HouseCardItem> {
        val json = prefs(context).getString(KEY_HOUSES_JSON, null) ?: return emptyList()
        return parseList(json)
    }

     // NOTE : 새 집 한 건을 목록에 추가하고 저장.
    fun addHouse(context: Context, item: HouseCardItem) {
        val list = getHouses(context).toMutableList()
        list.add(item)
        prefs(context).edit().putString(KEY_HOUSES_JSON, toJson(list)).apply()
    }

    // NOTE : 인덱스에 해당하는 집을 목록에서 제거하고 저장. (실제 앱에서는 고유 ID로 제거하는 방식이 더 일반적일 수 있음)
    // TODO : 현재는 화면 표시용 로컬 리스트 기준으로 인덱스로 삭제. 백엔드 연동 시 서버 삭제 API(예: houseId 기준)로 교체할 것.
    fun removeHouse(context: Context, index: Int) {
        val list = getHouses(context).toMutableList()
        if(index !in list.indices) return //// 잘못된 인덱스면 조용히 종료
        list.removeAt(index) // 해당 인덱스 삭제
        prefs(context).edit().putString(KEY_HOUSES_JSON, toJson(list)).apply() // 변경된 리스트 저장
    }


    // TODO : 수정 부분인데 -> 수정 부분은 화면으로 이동하게 한다음에 수정하는 걸로 생각중
    fun updateHouse(context: Context, index: Int, updatedItem: HouseCardItem) {

    }

    private fun toJson(list: List<HouseCardItem>): String {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            if (item.houseId != null) obj.put("houseId", item.houseId)
            obj.put("homeName", item.homeName)
            obj.put("address", item.address)
            if (item.ltv != null) obj.put("ltv", item.ltv)
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun parseList(json: String): List<HouseCardItem> {
        val list = mutableListOf<HouseCardItem>()
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val houseId = if (obj.has("houseId") && !obj.isNull("houseId")) obj.getLong("houseId") else null
            val homeName = obj.getString("homeName")
            val address = obj.getString("address")
            val ltv = if (obj.has("ltv") && !obj.isNull("ltv")) obj.getInt("ltv") else null
            list.add(HouseCardItem(houseId = houseId, homeName = homeName, address = address, ltv = ltv))
        }
        return list
    }
}
