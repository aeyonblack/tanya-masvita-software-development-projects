using System;
using System.Collections;
using UnityEngine;
using UnityEngine.SceneManagement;


/// <summary>
/// Shows loading screen between scene loadings
/// </summary>
public static class Loader 
{

    private class LoadingDummy : MonoBehaviour
    {

    }

    private static Action onLoaderCallback;

    private static AsyncOperation async;

    /// <summary>
    /// The of the loading scene
    /// </summary>
    private const string loadingScene = "Loading";

    /// <summary>
    /// Laods the specified scene with name = sceneName
    /// </summary>
    /// <param name="sceneName">name of the scene to load</param>
    public static void Load(string sceneName)
    {
        onLoaderCallback = () =>
        {
            GameObject obj = new GameObject("Loading Dummy");
            obj.AddComponent<LoadingDummy>().StartCoroutine(LoadAsync(sceneName));
        };

        // Load the loading scene/screen
        SceneManager.LoadScene(loadingScene);
    }

    private static IEnumerator LoadAsync(string sceneName)
    {
        yield return new WaitForSeconds(10f);
        async = SceneManager.LoadSceneAsync(sceneName); 

        while (!async.isDone)
        {
            yield return null;
        }
    }

    /// <summary>
    /// Triggered after the first update which lets the screen refresh
    /// </summary>
    public static void LoaderCallback()
    {
        if (onLoaderCallback != null)
        {
            onLoaderCallback();
            onLoaderCallback = null;
        }
    }

    /// <summary>
    /// Gets the progress from the loading async operation
    /// </summary>
    /// <returns>the progress</returns>
    public static float GetLoadingProgress()
    {
        return async != null ? async.progress : 1f;
    }
}
