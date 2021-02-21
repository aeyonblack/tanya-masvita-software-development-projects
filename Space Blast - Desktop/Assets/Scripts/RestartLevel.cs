using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class RestartLevel : MonoBehaviour
{
    public void Restart()
    {
        // This is obvious, Load the current scene
        // Function not working at all....
        // Few modifications to the code might to the trick...
        SceneManager.LoadScene(SceneManager.GetActiveScene().buildIndex);

    }
}
