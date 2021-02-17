using UnityEngine;

public class DestructedEnemy : MonoBehaviour, IDestructible
{
    public AudioClip DeathAuidoClip;

    public void OnDestruction(CharacterData player, CharacterStats enemy)
    {
        var source = AudioManager.GetWorldSFXSource();
        source.transform.position = transform.position;
        source.PlayOneShot(DeathAuidoClip);

        player.Experience.AddExperience(enemy.xpForKill);
        GameManager.instance.Gold.AddCurrency(enemy.goldForKill);
        DamagePopup.Create(transform.position, enemy.xpForKill, false, player, true);
    }
}
