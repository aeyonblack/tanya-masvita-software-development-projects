using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Utility primarily responsible for shaking the camera when the player fires a weapon. 
/// Can also be used to shake camera when the player gets damaged or is running or possibly walking. 
/// </summary>
public class CameraShaker : Singleton<CameraShaker>
{
    private float m_RemainingShakeTime;
    private float m_ShakeStrength;
    private Vector3 m_OriginalPosition;

    #region Monobehaviours

    protected override void Awake()
    {
        base.Awake();
        m_OriginalPosition = transform.localPosition;
    }

    private void Update()
    {
        if (m_RemainingShakeTime > 0)
        {
            m_RemainingShakeTime -= Time.deltaTime;

            if (m_RemainingShakeTime <= 0)
            {
                transform.localPosition = m_OriginalPosition;
            }
            else
            {
                Vector3 randomDirection = Random.insideUnitSphere;
                transform.localPosition = m_OriginalPosition + randomDirection * m_ShakeStrength;
            }
        }
    }

    #endregion

    #region Shake

    /// <summary>
    /// Shakes the camera for a fixed time
    /// </summary>
    /// <param name="time">Duration of shake</param>
    /// <param name="strength">Shake intensity</param>
    public void Shake(float time, float strength)
    {
        m_RemainingShakeTime = time;
        m_ShakeStrength = strength;
    }

    #endregion

}
