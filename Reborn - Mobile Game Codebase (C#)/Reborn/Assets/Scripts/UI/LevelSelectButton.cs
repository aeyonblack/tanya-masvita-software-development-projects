using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using UnityEngine.SceneManagement;

/// <summary>
/// The button for selecting a level
/// </summary>
[RequireComponent(typeof(Button))]
public class LevelSelectButton : MonoBehaviour, ISelectHandler
{
    /// <summary>
    /// Reference to the required button
    /// </summary>
    protected Button m_Button;

    /// <summary>
    /// Reference to the play button
    /// </summary>
    public Button PlayButton;

    /// <summary>
    /// Reference to the button text
    /// </summary>
    public Text PlayButtonText;

    /// <summary>
    /// Displays the name of the level
    /// </summary>
    public Text TitleDisplay;

    /// <summary>
    /// Displays Image for the level
    /// </summary>
    public RawImage LevelImage;

    /// <summary>
    /// Describes the level objectives 
    /// </summary>
    public Text Description;

    /// <summary>
    /// Current level state, Play, Locked, Complete
    /// </summary>
    public Text Status;

    /// <summary>
    /// Time spent in level
    /// </summary>
    public Text Time;

    public Transform WarningScreen;

    /// <summary>
    /// Enabled when selected level is locked
    /// </summary>
    public Image LockImage;


    /// <summary>
    /// The data concerning the level this button displays
    /// </summary>
    protected LevelItem m_Item;

    private LevelList levelList;

    /// <summary>
    /// When the user clicks the button change the scene
    /// </summary>
    public void ButtonClicked()
    {
        if (!HasStartingWeapon())
        {
            if (WarningScreen != null)
                Instantiate(WarningScreen);
            return;
        }
        LoadScene();
    }

    /// <summary>
    /// Check if the player has selected any starting weapons
    /// </summary>
    /// <returns>true if player has atleast one starting weapon</returns>
    public bool HasStartingWeapon()
    {
        for (int i = 0; i < GameManager.instance.StartingWeapons.Length; i++)
        {
            WeaponConfiguration weapon = GameManager.instance.StartingWeapons[i];
            if (weapon != null)
                return true;
        }

        return false;
    }

    /// <summary>
    /// Close the warning window
    /// </summary>
    public void CloseWarning()
    {
        WarningScreen.gameObject.SetActive(false);
    }

    /// <summary>
    /// Assigns data from item to button
    /// </summary>
    /// <param name="item"></param>
    public void Initialize(LevelItem item)
    {
        LazyLoad();
        if (TitleDisplay == null)
        {
            return;
        }
        m_Item = item;
        TitleDisplay.text = item.name;
        CheckLevelAvailability();
        /**if (item.Locked)
        {
            // Check if the previous level has been completed
            // TODO : FIX THIS STUFF LATER
            
            Time.gameObject.SetActive(false);
            Description.text = "Complete previous level to unlock";
            PlayButton.interactable = false;
            PlayButton.image.color = new Color(145, 145, 145);
            PlayButtonText.text = "LOCKED";
            LockImage.gameObject.SetActive(true);
        }
        else
        {
            Time.gameObject.SetActive(true);
            Description.text = item.description;
            PlayButton.interactable = true;
            PlayButton.image.color = new Color(255, 255, 0);
            PlayButtonText.text = "PLAY!";
            LockImage.gameObject.SetActive(false);
        }**/
        HasPlayedState();
    }

    /// <summary>
    /// Checks if the specified level is locked or not
    /// </summary>
    protected void CheckLevelAvailability()
    {
        int index = levelList.IndexOf(m_Item);
        int previous = index - 1;
        if (previous >= 0)
        {
            LevelItem previousLevel = levelList[previous];
            float previousLevelTime = GameManager.instance.GetTimeForLevel(previousLevel.id);
            if (previousLevelTime <= 0 && !GameManager.instance.TestMode)
            {
                // Lock this level, previous level not complete yet
                Time.gameObject.SetActive(false);
                Description.text = "Complete previous level to unlock";
                PlayButton.interactable = false;
                PlayButton.image.color = new Color(145, 145, 145);
                PlayButtonText.text = "LOCKED";
                LockImage.gameObject.SetActive(true);

                Debug.Log("Level - " + m_Item.name + " is locked");
            } 
            else
            {
                // Previous level is complete, unlock this level
                Time.gameObject.SetActive(true);
                Description.text = m_Item.description;
                PlayButton.interactable = true;
                PlayButton.image.color = new Color(255, 255, 0);
                PlayButtonText.text = "PLAY!";
                LockImage.gameObject.SetActive(false);

                Debug.Log("Level - " + m_Item.name + " is unlocked");
            }
        }
        else
        {
            // This is the first level in the list, its unlocked by default
            Time.gameObject.SetActive(true);
            Description.text = m_Item.description;
            PlayButton.interactable = true;
            PlayButton.image.color = new Color(255, 255, 0);
            PlayButtonText.text = "PLAY!";
            LockImage.gameObject.SetActive(false);

            Debug.Log("Level - " + m_Item.name + " is the first level and is unlocked by default");
        }
    }

    /// <summary>
    /// Configures the feedback concerning whether the player has played or not
    /// </summary>
    protected void HasPlayedState()
    {
        GameManager manager = GameManager.instance;
        if (manager == null)
        {
            return;
        }
        if (Time.gameObject.activeSelf)
        {
            float levelTime = manager.GetTimeForLevel(m_Item.id);
            int minutes = (int)levelTime / 60;
            int seconds = (int)levelTime % 60;
            Time.text = minutes + " min : " + seconds + " sec ";
        }
    }

    /// <summary>
    /// Change the scene to the scene name specified by the LevelItem
    /// </summary>
    protected void LoadScene()
    {
        Loader.Load(m_Item.sceneName);
    }

    /// <summary>
    /// Ensure the button is not null
    /// </summary>
    protected void LazyLoad()
    {
        if (m_Button == null)
        {
            m_Button = GetComponent<Button>();
        }
        levelList = GameManager.instance.LevelList;
    }

    public void OnSelect(BaseEventData eventData)
    {
        
    }

    protected void OnDestroy()
    {
        if (m_Button != null)
        {
            m_Button.onClick.RemoveAllListeners();
        }
    }
}
