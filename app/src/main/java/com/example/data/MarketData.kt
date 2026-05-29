package com.example.data

import android.content.Context

data class MatchRecord(
    val id: String,
    val playerChar: String,
    val opponentChar: String,
    val result: String, // "VICTORY", "DEFEAT"
    val arena: String,
    val date: String,
    val score: Int
)

class LocalGameStore(context: Context) {
    private val prefs = context.getSharedPreferences("AlamgirVsKhorshedPrefs", Context.MODE_PRIVATE)

    fun getWins(): Int = prefs.getInt("total_wins", 0)
    fun incrementWins() {
        prefs.edit().putInt("total_wins", getWins() + 1).apply()
    }

    fun getMatchesPlayed(): Int = prefs.getInt("matches_played", 0)
    fun incrementMatches() {
        prefs.edit().putInt("matches_played", getMatchesPlayed() + 1).apply()
    }

    fun getHighScore(): Int = prefs.getInt("high_score", 0)
    fun setHighScore(score: Int) {
        if (score > getHighScore()) {
            prefs.edit().putInt("high_score", score).apply()
        }
    }

    fun getBgmVolume(): Float = prefs.getFloat("bgm_volume", 0.5f)
    fun setBgmVolume(value: Float) {
        prefs.edit().putFloat("bgm_volume", value).apply()
    }

    fun getSfxVolume(): Float = prefs.getFloat("sfx_volume", 0.7f)
    fun setSfxVolume(value: Float) {
        prefs.edit().putFloat("sfx_volume", value).apply()
    }

    fun isAiDifficult(): Boolean = prefs.getBoolean("ai_difficulty", false)
    fun setAiDifficult(value: Boolean) {
        prefs.edit().putBoolean("ai_difficulty", value).apply()
    }
}
