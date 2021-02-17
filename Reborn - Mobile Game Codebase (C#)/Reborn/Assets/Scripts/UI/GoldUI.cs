using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Displays the amount of gold available
/// </summary>
public class GoldUI : MonoBehaviour
{

    /// <summary>
    /// Used for displaying amount of gold
    /// </summary>
    public Text Display;

    private int current;

    private int target;

    protected Gold m_Gold;

    private void Start()
    {
        if (GameManager.instance != null)
        {
            m_Gold = GameManager.instance.Gold;

            UpdateDisplay();
            m_Gold.currencyChanged += UpdateDisplay;
        }
    }

    private void Update()
    {
        if (current < target)
        {
            current += 1;
            Display.text = current.ToString();
        }
        else if (current > target)
        {
            current -= 1;
            Display.text = current.ToString();
        }
    }

    /// <summary>
    /// Populate display with updated values
    /// </summary>
    private void UpdateDisplay()
    {
        target = m_Gold.currentAmount;
        current = target >= 100 ? target - 100 : 0;
    }

    /// <summary>
    /// Unsubscribe
    /// </summary>
    private void OnDestroy()
    {
        if (m_Gold != null)
        {
            m_Gold.currencyChanged -= UpdateDisplay;
        }
    }
}
