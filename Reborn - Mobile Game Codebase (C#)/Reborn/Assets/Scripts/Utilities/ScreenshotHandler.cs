using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Utility for taking screenshots
/// </summary>
public class ScreenshotHandler : MonoBehaviour
{
    private static ScreenshotHandler instance;

    public Camera cam;
    private bool takeScreenshotOnNextFrame;

    private void Awake()
    {
        instance = this;
        //
        cam = gameObject.GetComponent<Camera>();
    }

    private void OnPostRender()
    {
        if (takeScreenshotOnNextFrame)
        {
            takeScreenshotOnNextFrame = false;
            RenderTexture texture = cam.targetTexture;

            Texture2D result = new Texture2D(texture.width, texture.height, TextureFormat.ARGB32, false);
            Rect rect = new Rect(0, 0, texture.width, texture.height);
            result.ReadPixels(rect, 0, 0);

            byte[] bytes = result.EncodeToPNG();
            System.IO.File.WriteAllBytes(Application.dataPath + "/screenshot.png", bytes);
            Debug.Log("Screenshot saved");

            RenderTexture.ReleaseTemporary(texture);
            cam.targetTexture = null;
        }
    }


    private void TakeScreenshot(int width, int height)
    {
        cam.targetTexture = RenderTexture.GetTemporary(width, height, 16);
        takeScreenshotOnNextFrame = true;
    }

    public static void Take(int width, int height)
    {
        instance.TakeScreenshot(width, height);
    }
}
