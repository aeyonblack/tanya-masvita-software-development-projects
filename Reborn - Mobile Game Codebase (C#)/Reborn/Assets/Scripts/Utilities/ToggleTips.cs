using UnityEngine;
using UnityEngine.UI;

public class ToggleTips : MonoBehaviour
{
    public Transform TipsUI;

    public Text ButtonText;

    private void Start()
    {
        ButtonText.text = TipsUI.gameObject.activeSelf ? "HIDE" : "SHOW";
    }

    /// <summary>
    /// Hide or show the tips UI
    /// </summary>
    public void Toggle()
    {
        if (TipsUI.gameObject.activeSelf)
        {
            TipsUI.gameObject.SetActive(false);
            ButtonText.text = "SHOW";
        }
        else
        {
            TipsUI.gameObject.SetActive(true);
            ButtonText.text = "HIDE";
        }
    }
}
