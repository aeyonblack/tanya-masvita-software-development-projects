using UnityEngine.UI;

/// <summary>
/// Simple options menu for setting volumes and removing ads
/// Projected to grow more complex as more features are added to the game
/// </summary>
public class OptionsMenu : MainMenuPage
{
    /// <summary>
    /// Used for adjusting the master volume
    /// </summary>
    public Slider MasterSlider;

    /// <summary>
    /// Used for adjusting the sfx volume
    /// </summary>
    public Slider SfxSlider;

    /// <summary>
    /// Used for adjusting the music volume
    /// </summary>
    public Slider MusicSlider;

    /// <summary>
    /// Used for adjusting the touch sensitivity
    /// </summary>
    public Slider TouchSensitivitySlider;

    /// <summary>
    /// Allows the player to remove ads by paying a predetermined amount
    /// </summary>
    public Button RemoveAdsButton;

    public Text RemoveAdsButtonText;

    private void Start()
    {
        if (GameManager.instance == null)
        {
            return;
        }

        RemoveAdsButtonText.text = GameManager.instance.AdsRemoved() ? "NO ADS" : "BUY";
        RemoveAdsButton.interactable = !GameManager.instance.AdsRemoved();
    }

    /// <summary>
    /// Invoked when player clicks remove ads button 
    /// </summary>
    public void RemoveADS()
    {
        IAPManager.instance.BuyRemoveAds();
    }

    /// <summary>
    /// Called when the player removes ads successfully
    /// </summary>
    public void DisableRemoveAdsButton()
    {
        RemoveAdsButtonText.text = "NO ADS";
        RemoveAdsButton.interactable = false;
    }

    /// <summary>
    /// Event fired when sliders change
    /// </summary>
    public void UpdateVolumes()
    {
        float masterVolume, sfxVolume, musicVolume;
        GetSliderVolumes(out masterVolume, out sfxVolume, out musicVolume);

        if (GameManager.instanceExists)
        {
            GameManager.instance.SetVolumes(masterVolume, sfxVolume, musicVolume, false);
        }
    }

    /// <summary>
    /// Event fired when touch slider changes
    /// </summary>
    public void UpdateTouchSensitivity()
    {
        float touchSensitivity;
        GetTouchSensitivity(out touchSensitivity);

        if (GameManager.instanceExists)
        {
            GameManager.instance.SetTouchSensitivity(touchSensitivity,false);
        }
    }

    /// <summary>
    /// Set initial slider volumes
    /// </summary>
    public override void Show()
    {
        if (GameManager.instanceExists)
        {
            float master, sfx, music, touchSensitivity;
            GameManager.instance.GetVolumes(out master, out sfx, out music);
            GameManager.instance.GetTouchSensitivity(out touchSensitivity);

            if (MasterSlider != null)
            {
                MasterSlider.value = master;
            }

            if (SfxSlider != null)
            {
                SfxSlider.value = sfx;
            }

            if (MusicSlider != null)
            {
                MusicSlider.value = music;
            }

            if (TouchSensitivitySlider != null)
            {
                TouchSensitivitySlider.value = touchSensitivity;
            }

            IAPManager.instance.AdsRemoved += DisableRemoveAdsButton;
        }
        base.Show();
    }

    /// <summary>
    /// Persist volumes
    /// </summary>
    public override void Hide()
    {
        float masterVolume, sfxVolume, musicVolume, touchSensitivity;
        GetSliderVolumes(out masterVolume, out sfxVolume, out musicVolume);
        GetTouchSensitivity(out touchSensitivity);

        if (GameManager.instanceExists)
        {
            GameManager.instance.SetVolumes(masterVolume, sfxVolume, musicVolume, true);
            GameManager.instance.SetTouchSensitivity(touchSensitivity, true);
        }

        IAPManager.instance.AdsRemoved -= DisableRemoveAdsButton;

        base.Hide();
    }

    /// <summary>
    /// Retrieve values from sliders
    /// </summary>
    /// <param name="masterVolume"></param>
    /// <param name="sfxVolume"></param>
    /// <param name="musicVolume"></param>
    private void GetSliderVolumes(out float masterVolume, out float sfxVolume, out float musicVolume)
    {
        masterVolume = MasterSlider != null ? MasterSlider.value : 1;
        sfxVolume = SfxSlider != null ? SfxSlider.value : 1;
        musicVolume = MusicSlider != null ? MusicSlider.value : 1;
    }

    /// <summary>
    /// Retrieves the touch sensitivity value from the slider
    /// </summary>
    /// <param name="touchSensitivity">the touch sensitivity value to retrieve</param>
    private void GetTouchSensitivity(out float touchSensitivity)
    {
        touchSensitivity = TouchSensitivitySlider != null ? TouchSensitivitySlider.value : 1;
    }
}
