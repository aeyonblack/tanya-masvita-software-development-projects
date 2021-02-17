using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Small Menu UI for displaying player progress and stats
/// </summary>
public class GameStatsUI : MonoBehaviour
{
    public Text PlayerLevel;

    public Text TotalXp;

    public Text Time;

    public Text Kills;

    public Text DamageDone;

    public Text DamageTaken;

    public Text StonesCollected;

    public Text Hits;

    public Text Score;

    public Slider XPToNextLevelSlider;

    private string formattedTime;

    private bool isAnimating;

    private float xp;

    public void Start()
    {
        FormatTime();
        Time.text = formattedTime;
        PlayerLevel.text = GameManager.instance.GetPlayerLevel().ToString();
        TotalXp.text = GameManager.instance.GetPlayerXP().ToString();
        Kills.text = GameManager.instance.GetKills().ToString();
        DamageDone.text = GameManager.instance.GetDamageDone().ToString();
        DamageTaken.text = GameManager.instance.GetDamageTaken().ToString();
        StonesCollected.text = GameManager.instance.GetStonesCollected().ToString();
        Hits.text = GameManager.instance.GetHits().ToString();
        Score.text = GameManager.instance.GetScore().ToString();
        SetUpXPSlider();

        isAnimating = true;
        xp = 0;
    }

    private void Update()
    {
        if (isAnimating)
        {
            if (xp < GameManager.instance.GetExperienceNormalized())
            {
                xp += 0.01f;
            }
            else
            {
                isAnimating = false;
            }
            XPToNextLevelSlider.value = xp;
        }
    }

    private void FormatTime()
    {
        float time = GameManager.instance.GetTime();
        int min = (int)time / 60;
        int hours = min / 60;
        int min_f = min % 60;
        formattedTime = hours.ToString() + " hrs : " + min_f + " mins"; 
    }

    private void SetUpXPSlider()
    {
        XPToNextLevelSlider.minValue = 0f;
        XPToNextLevelSlider.maxValue = 1f;
    }
}
