using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Shows Extra life offer everytime the player reaches 0 health
/// </summary>
public class ExtraLifeUI : MonoBehaviour
{
    private float totalTime;

    private int seconds;

    /// <summary>
    /// Displays amount of time remaining to take offer
    /// </summary>
    public Text Timer;

    public void SetTime()
    {
        totalTime = 6f;
        seconds = (int)(totalTime % 60);
        Timer.text = seconds.ToString();
    }

    private void Start()
    {
        SetTime();
    }

    private void Update()
    {
        if (totalTime > 0)
        {
            totalTime -= Time.deltaTime;
            seconds = (int)(totalTime % 60);
            Timer.text = seconds.ToString();
        }
    }
}
