using UnityEngine;

public class DestructedRagdoll : MonoBehaviour, IDestructible
{
    /// <summary>
    /// Upgrade this to disolve
    /// </summary>
    public Ragdoll Ragdoll;
    public float Force;
    public float Lift;

    public void OnDestruction(CharacterData player, CharacterStats enemy)
    {
        /**var ragdoll = Instantiate(Ragdoll, transform.position, transform.rotation);
        Helpers.RecursiveLayerChange(ragdoll.transform, LayerMask.NameToLayer("Enemy"));

        var vectorFromPlayer = transform.position - Controller.instance.transform.position;
        vectorFromPlayer.Normalize();
        vectorFromPlayer.y += Lift;

        ragdoll.AddForce(vectorFromPlayer * Force);**/

        player.Experience.AddExperience(enemy.xpForKill);
        //DamagePopup.Create(transform.position, enemy.xpForKill, false, player, true);
    }
}
