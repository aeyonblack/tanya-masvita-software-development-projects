using System.Collections;
using UnityEngine;
using UnityEngine.Advertisements;

public enum Position
{
    TOP_CENTER,
    BOTTOM_LEFT
}

/// <summary>
/// Manages the displaying of ads
/// </summary>
public class BannerAd : MonoBehaviour
{
#if UNITY_IOS
    private string gameId = "3646659";
#elif UNITY_ANDROID
    private string gameId = "3646658";
#endif

    public bool testMode = true;

    public Position adPosition;

    /// <summary>
    /// Placement id for banner ad 
    /// </summary>
    private string placementId = "loadingBanner";

    /// <summary>
    /// Show banner ad on start
    /// </summary>
    private void Start()
    {
        if (GameManager.instance == null)
        {
            return;
        }

        if (GameManager.instance.AdsRemoved())
        {
            return;
        }

        Debug.Log("Used for Ad initialization");
        Advertisement.Initialize(gameId, testMode);
        //StartCoroutine(ShowBanner());
    }

    /// <summary>
    /// Show banner ad when advertisments is initialized
    /// </summary>
    /// <returns></returns>
    private IEnumerator ShowBanner()
    {
        while (!Advertisement.isInitialized)
        {
            yield return new WaitForSeconds(1f);
        }
        if (adPosition == Position.TOP_CENTER)
        {
            Advertisement.Banner.SetPosition(BannerPosition.TOP_CENTER);
        }
        else
        {
            Advertisement.Banner.SetPosition(BannerPosition.BOTTOM_LEFT);
        }
        Advertisement.Banner.Show(placementId);
    }

}
