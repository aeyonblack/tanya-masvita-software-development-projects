using UnityEngine;
using UnityEngine.UDP;

public class InitListener : IInitListener
{
    public void OnInitialized(UserInfo userInfo)
    {
        Debug.Log("Initialization succeeded");
    }

    public void OnInitializeFailed(string message)
    {
        Debug.Log("Initialization failed " + message);
    }
}
