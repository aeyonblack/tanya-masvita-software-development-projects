using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AnimationControllerDispatcher : MonoBehaviour
{
    public interface IAttackFrameReceiver
    {
        void AttackFrame();
    }

    public MonoBehaviour AttackFrameReceiver;

    private IAttackFrameReceiver m_AttackReceiver;

    private void Awake()
    {
        if (AttackFrameReceiver != null)
        {
            m_AttackReceiver = AttackFrameReceiver as IAttackFrameReceiver;
        }
    }

    private void AttackEvent()
    {
        m_AttackReceiver?.AttackFrame();
    }

}
