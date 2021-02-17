using UnityEngine;

public class DestroyObject : MonoBehaviour, IDestructible
{
    public void OnDestruction(CharacterData player, CharacterStats enemy)
    {
        Destroy(gameObject);
    }
}
