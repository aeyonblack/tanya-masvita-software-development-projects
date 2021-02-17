using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Displays and handles the level failed UI
/// </summary>
public class LevelFailedUI : MonoBehaviour
{
    /// <summary>
    /// Reference to the level manager
    /// </summary>
    private LevelManager LevelManager;

    /// <summary>
    /// Displays the level failed message
    /// </summary>
    public Text LevelFailedMessageTextField;

    /// <summary>
    /// Message to display when level failed
    /// </summary>
    private string levelFailedMessage;

    private void Start()
    {
        LazyLoad();
        levelFailedMessage = (int)LevelManager.TimeLeft == 0 ? "You ranout of time!!!" : "You were killed!!";
        LevelFailedMessageTextField.text = levelFailedMessage;
    }

    /// <summary>
    /// Invoked when player clicks Retry Level button
    /// Allows the player to replay a failed level
    /// </summary>
    public void RetryLevel()
    {
        Debug.Log("[RETRYLEVEL]");
    }

    /// <summary>
    /// Invoked when player clicks the Main Menu button
    /// Takes the player to the main game menu
    /// </summary>
    public void GoToMainMenu()
    {
        Debug.Log("[GOTOMAINMENU]");
    }

    /// <summary>
    /// Ensure Level manager is not null
    /// </summary>
    private void LazyLoad()
    {
        if (LevelManager == null && (LevelManager.instanceExists))
        {
            LevelManager = LevelManager.instance;
        }
    }
}
