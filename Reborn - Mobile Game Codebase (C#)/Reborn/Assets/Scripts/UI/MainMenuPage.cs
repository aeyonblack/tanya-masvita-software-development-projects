using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Class for menu pages that turn on and off
/// </summary>
public class MainMenuPage : MonoBehaviour, IMainMenuPage
{
    /// <summary>
    /// Canvas to disable the view
    /// </summary>
    public Canvas Canvas;

    /// <summary>
    /// Reference to the menu button
    /// </summary>
    public Button MenuButton;

    /// <summary>
    /// Text for the menu button
    /// </summary>
    public Text MenuButtonText;

    /// <summary>
    /// Deactivates this page
    /// </summary>
    public virtual void Hide()
    {
        if (Canvas != null)
        {
            Canvas.enabled = false;
        }
        
        if (gameObject.activeSelf)
        {
            gameObject.SetActive(false);
        }

        if (MenuButton != null)
        {
            MenuButton.image.color = new Color(255, 255, 255, 0);
            MenuButtonText.color = new Color(255, 255, 255);
            MenuButtonText.fontSize = 25;
        }

    }

    /// <summary>
    /// Activates this page
    /// </summary>
    public virtual void Show()
    {
        if (Canvas != null)
        {
            Canvas.enabled = true;
        }
        
        if (!gameObject.activeSelf)
        {
            gameObject.SetActive(true);
        }

        if (MenuButton != null)
        {
            MenuButton.image.color = new Color(255, 255, 255, 255);
            MenuButtonText.color = new Color(0, 0, 0);
            MenuButtonText.fontSize = 27;
        }
    }

}
