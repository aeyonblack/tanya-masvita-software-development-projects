using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Advertisements;

/// <summary>
/// Allows players to opt in to watch rewarded ads
/// to recieve specific rewards
/// </summary>
[RequireComponent(typeof(Button))]
public class RewardedAdsButton : MonoBehaviour, IUnityAdsListener
{
    /// <summary>
    /// Placement id for the ad attached to this button
    /// </summary>
    public string placementId;

    public bool testMode;

    private const string extraLifePlacementId = "ExtraLife";

    private const string extraTimePlacementId = "ExtraTime";

    private const string doubleGoldPlacementId = "doubleGold";

    private const string extraGemsPlacementId = "extraGems";

#if UNITY_IOS
    private string gameId = "3646659";
#elif UNITY_ANDROID
    private string gameId = "3646658";
#endif

    /// <summary>
    /// Reference to the extra life screen
    /// </summary>
    public ExtraLifeUI ExtraLifeScreen;

    /// <summary>
    /// Reference to the extra time screen
    /// </summary>
    public ExtraTimeUI ExtraTimeScreen;

    private CharacterData player;

    private CharacterDamage playerDamage;

    /// <summary>
    /// The button for this script
    /// </summary>
    private Button button;


    private void Start()
    {
        if (LevelManager.instance == null)
        {
            return;
        }

        button = GetComponent<Button>();
        button.interactable = Advertisement.IsReady(placementId);

        player = LevelManager.instance.Player;
        playerDamage = player.GetComponent<CharacterDamage>();

        // Initialize the ads listener and service
        Advertisement.AddListener(this);
        Advertisement.Initialize(gameId, testMode);
    }

    /// <summary>
    /// Show Ad with specified placement id
    /// </summary>
    public void ShowRewardedVideo()
    {
        Advertisement.Show(placementId);
    }

    // Implement IUnityAdsListener interface methods
    public void OnUnityAdsReady(string placementId)
    {
        if (this.placementId == placementId)
        {
            button.interactable = true;
        }
    }

    public void OnUnityAdsDidError(string message)
    {
        Debug.LogError("[UNITYADS-CUSTOM] " + message);
    }

    public void OnUnityAdsDidStart(string placementId)
    {
        if (LevelManager.instance.CurrentLevelState == LevelManager.LevelState.Active)
        {
            LevelManager.instance.PauseGame();
        }

        LevelManager.instance.enabled = false;
    }

    public void OnUnityAdsDidFinish(string placementId, ShowResult showResult)
    {

        LevelManager.instance.enabled = true;

        if (LevelManager.instance.CurrentLevelState == LevelManager.LevelState.Active)
        {
            LevelManager.instance.UnPauseGame();
        }

        if (showResult == ShowResult.Finished)
        {
            Debug.Log("Ad Finished");
            Debug.Log(placementId);
            if (this.placementId == placementId)
            {
                switch (placementId)
                {
                    case extraLifePlacementId:
                        RewardPlayerWithExtraLife();
                        break;
                    case extraTimePlacementId:
                        RewardPlayerWithExtraTime();
                        break;
                    case doubleGoldPlacementId:
                        RewardPlayerWithDoubleGold();
                        break;
                    case extraGemsPlacementId:
                        RewardPlayerWithExtraGems();
                        break;
                }
            }
        }
        else if (showResult == ShowResult.Skipped)
        {
            // Find a way to debug on mobile
            Debug.Log("Ad skipped, Player will not be rewarded");
        }
        else
        {
            Debug.Log("Ad failed");
        }
    }

    /// <summary>
    /// Gives the player an extra life, by increasing the health to 100,
    /// and re-animating the player by setting alive to true
    /// </summary>
    private void RewardPlayerWithExtraLife()
    {
        StopCoroutine(playerDamage.MakeExtraLifeOffer(null));
        Controller.instance.WeaponPosition.gameObject.SetActive(true);
        player.Stats.alive = true;
        player.ModifyHealth(100);
        player.ModifyResistance(player.Stats.maxResistance);
        if (ExtraLifeScreen.gameObject.activeSelf)
        {
            ExtraLifeScreen.gameObject.SetActive(false);
            ExtraLifeScreen.SetTime();
        }
    }

    /// <summary>
    /// Gives the player an extra 2 minutes by adding 120
    /// level remaining time
    /// </summary>
    private void RewardPlayerWithExtraTime()
    {
        if (LevelManager.instanceExists)
        {
            LevelManager.instance.TimeLeft += 120f;
            if (!LevelManager.instance.enabled) LevelManager.instance.enabled = true;
            if (ExtraTimeScreen.gameObject.activeSelf)
            {
                ExtraTimeScreen.gameObject.SetActive(false);
                ExtraTimeScreen.SetTime();
            }
            LevelManager.instance.routineDone = false;
        }
    }

    /// <summary>
    /// Doubles the amount of gold a player has earned in the level
    /// Players can double their gold as much as they want
    /// If the player earns x amount of gold in level, they get +2x for every ad watched.
    /// </summary>
    private void RewardPlayerWithDoubleGold()
    {
        int doubleGold = LevelManager.instance.LevelStats.goldEarned;
        GameManager.instance.UpdateGold(doubleGold);
    }

    /// <summary>
    /// Rewards the player with at least two gems for every ad watched
    /// at the end of a level
    /// </summary>
    private void RewardPlayerWithExtraGems()
    {
        int doubleGems = 2;
        GameManager.instance.UpdateGems(doubleGems);
    }

    
}
