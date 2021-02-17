using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Displays the amount of gems available
/// </summary>
public class GemsUI : MonoBehaviour
{
    public Text Display;

    private int current;

    private int target;

    protected Gems m_Gems;

    private void Start()
    {
        if (GameManager.instance != null)
        {
            m_Gems = GameManager.instance.Gems;

            UpdateDisplay();
            m_Gems.currencyChanged += UpdateDisplay;
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
        target = m_Gems.currentAmount;
        current = target >= 100 ? target - 100 : 0;
    }

    /// <summary>
    /// Unsubscribe
    /// </summary>
    private void OnDestroy()
    {
        if (m_Gems != null)
        {
            m_Gems.currencyChanged -= UpdateDisplay;
        }
    }
}
