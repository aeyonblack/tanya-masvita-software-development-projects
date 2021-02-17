using UnityEngine;

/// <summary>
/// Helper class contains diverse functions to avoid repeating common actions 
/// </summary>
public class Helpers
{
    /// <summary>
    /// Recursively change the layer of a gameobject and all other 
    /// objects it wraps
    /// </summary>
    /// <param name="root">root gameobject</param>
    /// <param name="layer">layer to set</param>
    public static void RecursiveLayerChange(Transform root, int layer)
    {
        root.gameObject.layer = layer;
        foreach (Transform t in root)
            RecursiveLayerChange(t, layer);
    }

    public static void RecursiveReplaceMaterial(GameObject gameObject, Material material)
    {
        var meshR = gameObject.GetComponentInChildren<SkinnedMeshRenderer>();

        if (meshR != null)
        {
            if (meshR.materials.Length > 1)
            {
                var newMaterials = new Material[meshR.materials.Length];
                for (var i = 0; i < newMaterials.Length; i++)
                {
                    newMaterials[i] = material;
                }
                meshR.materials = newMaterials;
            }
            else
            {
                meshR.material = material;
            }
        }

        if (gameObject.transform.childCount != 0)
        {
            for (int i = 0; i < gameObject.transform.childCount; i++)
            {
                RecursiveReplaceMaterial(gameObject.transform.GetChild(i).gameObject, material);
            }
        }
    }
}
