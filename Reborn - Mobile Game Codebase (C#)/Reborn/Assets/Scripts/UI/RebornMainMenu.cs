/// <summary>
/// Implementation of the main menu
/// </summary>
public class RebornMainMenu : MainMenu
{
    /// <summary>
    /// Reference to the start menu
    /// </summary>
    public MainMenuPage StartMenu;

    /// <summary>
    /// Reference to the level select screen
    /// </summary>
    public LevelSelectScreen LevelSelectScreen;

    /// <summary>
    /// Reference to the store screen
    /// </summary>
    public StoreScreen StoreScreen;

    /// <summary>
    /// Reference to the stats screen
    /// </summary>
    public StatsScreen StatsScreen;

    /// <summary>
    /// Reference to the options menu
    /// </summary>
    public OptionsMenu OptionsMenu;

    /// <summary>
    /// Reference to the social media screen
    /// </summary>
    public SocialScreen SocialScreen;

    /// <summary>
    /// Reference to the starting weapon screen
    /// </summary>
    public StartingWeaponScreen StartingWeaponScreen;

    protected virtual void Awake()
    {
        ShowStartMenu();
    }

    /// <summary>
    /// Shows the start menu
    /// </summary>
    public void ShowStartMenu()
    {
        Back(StartMenu);
    }

    /// <summary>
    /// Shows the level selection screen
    /// </summary>
    public void ShowLevelSelectScreen()
    {
        ChangePage(LevelSelectScreen);
    }

    /// <summary>
    /// Opens the store
    /// </summary>
    public void ShowStoreScreen()
    {
        ChangePage(StoreScreen);
    }

    /// <summary>
    /// Shows the options menu
    /// </summary>
    public void ShowOptionsMenu()
    {
        ChangePage(OptionsMenu);
    }

    /// <summary>
    /// Shows the stats menu
    /// </summary>
    public void ShowStatsScreen()
    {
        ChangePage(StatsScreen);
    }

    /// <summary>
    /// Show the social screen
    /// </summary>
    public void ShowSocialScreen()
    {
        ChangePage(SocialScreen);
    }

    /// <summary>
    /// Shows starting weapon screen
    /// </summary>
    public void ShowStartingWeaponScreen()
    {
        ChangePage(StartingWeaponScreen);
    }

}
