using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Handles the Store UI, purchasing and preview of store items
/// </summary>
public class StoreScreen : MainMenu, IMainMenuPage
{
    /// <summary>
    /// Reference to the screen that displays Gems in store
    /// </summary>
    public GemsScreen GemsScreen;

    /// <summary>
    /// Reference to the gold screen
    /// </summary>
    public GoldScreen GoldScreen;

    /// <summary>
    /// Reference to the weapon screen;
    /// </summary>
    public WeaponsScreen WeaponsScreen;

    /// <summary>
    /// Reference to the booster screen
    /// </summary>
    public BoosterScreen BoosterScreen;

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

    protected virtual void Awake()
    {
        ShowGemScreen();
    }

    /// <summary>
    /// Show gems UI
    /// </summary>
    public void ShowGemScreen()
    {
        Back(GemsScreen);
    }

    /// <summary>
    /// Show gold UI
    /// </summary>
    public void ShowGoldScreen()
    {
        ChangePage(GoldScreen);
    }

    /// <summary>
    /// Show weapons UI
    /// </summary>
    public void ShowWeaponScreen()
    {
        ChangePage(WeaponsScreen);
    }

    /// <summary>
    /// Show booster screen
    /// </summary>
    public void ShowBoosterScreen()
    {
        ChangePage(BoosterScreen);
    }

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
