using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Displays random tips every 10 seconds
/// </summary>
public class RandomTipGenerator : MonoBehaviour
{
    /// <summary>
    /// Text view to display the tip
    /// </summary>
    public Text Tip;

    /// <summary>
    /// Time between each tip
    /// </summary>
    public float Time;

    /// <summary>
    /// Array of possible tips to show
    /// </summary>
    [Multiline]
    public string[] Tips;

    private int index;

    private void Awake()
    {
        index = Random.Range(0, Tips.Length);
        if (Tips.Length > 0)
        {
            InvokeRepeating("ShowTip", Time, Time);
        }
    }

    /// <summary>
    /// Shows a random tip 
    /// </summary>
    public void ShowTip()
    {
        string tip = Tips[index];
        index = index == Tips.Length - 1 ? 0 : index + 1;
        Tip.text = tip;
    }


}
