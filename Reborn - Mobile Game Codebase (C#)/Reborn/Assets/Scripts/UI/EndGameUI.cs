using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using UnityEngine.SceneManagement;

/// <summary>
/// Updates the UI accordingly when the game ends
/// </summary>
public class EndGameUI : MonoBehaviour
{
    /// <summary>
    /// Clip to play when level completed successfully
    /// </summary>
    public AudioClip VictorySound;

    /// <summary>
    /// Clip to play when level failed
    /// </summary>
    public AudioClip DefeatSound;

    /// <summary>
    /// The containing panel of the end game UI
    /// </summary>
    public Canvas EndGameCanvas;

    /// <summary>
    /// UI to display when level completed successfully
    /// </summary>
    public LevelCompleteUI VictoryUI;

    /// <summary>
    /// UI to display when level failed
    /// </summary>
    public LevelFailedUI DefeatUI;

    /// <summary>
    /// Reference to the text diplayed at the header
    /// </summary>
    public Text EndGameHeader;

    /// <summary>
    /// Name of the main menu screen
    /// </summary>
    public string MenuSceneName = "MainMenu";

    /// <summary>
    /// Outter background image 
    /// </summary>
    public Image BackgroundOut;

    /// <summary>
    /// Inner background image
    /// </summary>
    public Image BackgroundIn;
    
    /// <summary>
    /// Outter background color to be used when player completes level
    /// </summary>
    public Color BackgroundColorOutWin;

    /// <summary>
    /// Inner backgroud color to be used when player completes level
    /// </summary>
    public Color BackgroundColorInWin;

    /// <summary>
    /// Outter background color for when the player loses
    /// </summary>
    public Color BackgroundColorOutLose;

    /// <summary>
    /// Inner background color for when the player loses
    /// </summary>
    public Color BackgroundColorInLose;

    /// <summary>
    /// Refrence to the level manager
    /// </summary>
    protected LevelManager LevelManager;

    private AudioSource audioSource;

    protected void Start()
    {
        LazyLoad();
        EndGameCanvas.enabled = false;
        VictoryUI.gameObject.SetActive(false);
        DefeatUI.gameObject.SetActive(false);

        audioSource = GetComponent<AudioSource>();

        LevelManager.LevelComplete += LevelCompletedSuccessfully;
        LevelManager.LevelFailed += LevelFailed;

    }

    /// <summary>
    /// Ensures level manager is not null
    /// </summary>
    protected void LazyLoad()
    {
        if (LevelManager == null && (LevelManager.instanceExists))
        {
            LevelManager = LevelManager.instance;
        }
    }

    /// <summary>
    /// Enable the level complete UI
    /// </summary>
    protected IEnumerator OpenLevelCompleteScreen()
    {
        audioSource.PlayOneShot(VictorySound);
        yield return new WaitForSeconds(2f);
        EndGameCanvas.enabled = true;
        VictoryUI.gameObject.SetActive(true);
        EndGameHeader.text = "Level Complete";
        BackgroundOut.color = BackgroundColorOutWin;
        BackgroundIn.color = BackgroundColorInWin;
    }

    /// <summary>
    /// Enable the level failed UI
    /// </summary>
    protected IEnumerator OpenLevelFailedScreen()
    {
        audioSource.PlayOneShot(DefeatSound);
        yield return new WaitForSeconds(1f);
        EndGameCanvas.enabled = true;
        DefeatUI.gameObject.SetActive(true);
        EndGameHeader.text = "Retry Level";
        BackgroundOut.color = BackgroundColorOutLose;
        BackgroundIn.color = BackgroundColorInLose;
    }

    /// <summary>
    /// Invoked when player clicks Retry Level button
    /// Allows the player to replay a failed level
    /// </summary>
    public void RetryLevel()
    {
        SafelyUnsubscribe();
        string currentSceneName = SceneManager.GetActiveScene().name;
        Loader.Load(currentSceneName);
    }

    /// <summary>
    /// Invoked when player clicks the Main Menu button
    /// Takes the player to the main game menu
    /// </summary>
    public void GoToMainMenu()
    {
        SafelyUnsubscribe();
        GameManager.instance.StartingWeapons = new WeaponConfiguration[3];
        Loader.Load(MenuSceneName);
    }

    /// <summary>
    /// Invoked when the player clicks next level button.
    /// Takes the player to the next level if it exists
    /// </summary>
    public void GoToNextLevel()
    {
        Debug.Log("[NEXTLEVEL]");
    }

    /// <summary>
    /// Occurs when level is completed 
    /// </summary>
    protected void LevelCompletedSuccessfully()
    {
        StartCoroutine(OpenLevelCompleteScreen());
    }

    /// <summary>
    /// Occurs when level is failed
    /// </summary>
    protected void LevelFailed()
    {
        StartCoroutine(OpenLevelFailedScreen());
    }

    /// <summary>
    /// Ensure all level manager events are unsubscribed from when necessary
    /// </summary>
    protected void SafelyUnsubscribe()
    {
        LazyLoad();
        LevelManager.LevelComplete -= LevelCompletedSuccessfully;
        LevelManager.LevelFailed -= LevelFailed;
    }

    /// <summary>
    /// Safely unsubscribe from events
    /// </summary>
    protected void OnDestroy()
    {
        SafelyUnsubscribe();
    }

}
