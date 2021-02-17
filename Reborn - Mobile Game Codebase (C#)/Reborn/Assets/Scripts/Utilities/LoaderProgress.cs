using UnityEngine;
using UnityEngine.UI;

public class LoaderProgress : MonoBehaviour
{
    private Slider loadingProgress;

    private void Awake()
    {
        loadingProgress = GetComponent<Slider>();
    }

    private void Update()
    {
        loadingProgress.value = Loader.GetLoadingProgress();
    }
}
