using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Responsible for basic player controls, movement, jumping, shooting
/// </summary>
public class Controller : Singleton<Controller>
{

    #region Variable Declarations

    [Header("Cameras")]
    public Camera MainCamera;
    public Camera WeaponCamera;
    public Transform CameraPosition;
    public Transform WeaponPosition;
    public float DefaultFOV;
    public float ScopeFOV;
    public float MaxFOV;

    [Header("Controller")]
    public Joystick Joystick;
    public FixedTouchField TouchField;
    public FixedButton FireButton;
    public FixedButton FireButton2;
    public FixedButton JumpButton;
    public FixedButton ScopeButton;
    public FixedButton SprintButton;

    [Header("Reticle Control")]
    public RectTransform Reticle;
    public float RestingSize;
    public float MaxSize;
    public float ReticleSpeed;
    private float m_CurrentReticleSize;

    [Header("Control Settings")]
    [HideInInspector] public float MouseSensitivity = 3.5f;
    public float PlayerSpeed = 5f;
    public float RunningSpeed = 7f;
    public float JumpSpeed = 5f;

    [Header("Audio")]
    public AudioClip JumpingClip;
    public AudioClip LandingClip;

    [Header("Scope")]
    public GameObject ScopeOverlay;
    public Slider ZoomSlider;

    public float Speed { get; private set; }

    [HideInInspector]public AudioSource AudioSource;
    public CharacterData Data => m_CharacterData;
    public bool Grounded => m_Grounded;
    public bool Running => m_IsRunning;

    private CharacterData m_CharacterData;
    private CharacterController m_CharacterController;
    private bool m_IsRunning;
    private bool m_Grounded;
    private float m_GroundedTimer;
    private float m_SpeedAtJump;

    private float m_VerticalSpeed = 0.0f;
    private float m_VerticalAngle;
    private float m_HorizontalAngle;

    #endregion

    private Vector3 move;
    private Vector3 currentAngles;
    private float actualSpeed;
    private float usedSpeed;
    private float turnPlayer;

    private bool isScoped = false;

    private float touchSensitivity;

    #region Monobehaviours

    /// <summary>
    /// Start method initializes control systems
    /// </summary>
    private void Start()
    {
        //Cursor.lockState = CursorLockMode.Locked;
        //Cursor.visible = false;

        m_Grounded = true;
        m_IsRunning = false;

        actualSpeed = 0f;
        usedSpeed = 0f;
        turnPlayer = 0f;

        currentAngles = Vector3.zero;

        MainCamera.transform.SetParent(CameraPosition, false);
        MainCamera.transform.localPosition = Vector3.zero;
        MainCamera.transform.localRotation = Quaternion.identity;

        MainCamera.fieldOfView = DefaultFOV;

        ZoomSlider.minValue = 0;
        ZoomSlider.maxValue = 50;

        m_CharacterData = GetComponent<CharacterData>();
        m_CharacterController = GetComponent<CharacterController>();
        AudioSource = GetComponent<AudioSource>();

        m_VerticalAngle = 0.0f;
        m_HorizontalAngle = transform.localEulerAngles.y;

        m_CharacterData.Initialize();

        // Move to GameManager 
        AudioManager.Init();

        GameManager.instance.GetTouchSensitivity(out touchSensitivity);
        Debug.Log("TOUCH SENSITIVTY = " + touchSensitivity);
    }

    /// <summary>
    /// Updates control systems every frame
    /// </summary>
    private void Update()
    {
        if (!m_CharacterData.Stats.alive)
        {
            return;
        }

        bool wasGrounded = m_Grounded;
        bool loosedGrounding = false;

        if (!m_CharacterController.isGrounded)
        {
            if (m_Grounded)
            {
                m_GroundedTimer += Time.deltaTime;

                if (m_GroundedTimer >= 0.5f)
                {
                    loosedGrounding = true;
                    m_Grounded = false;
                }
            }
        }
        else
        {
            m_GroundedTimer = 0.0f;
            m_Grounded = true;
        }

        Speed = 0;
        move = Vector3.zero;
        // TODO : [START IF]Possibly add the if (!Pause and !LockControl) statement
        MovePlayer(loosedGrounding, wasGrounded, move);
        UseWeapon();
        AdjustReticleSize();
        // TODO : [END IF]Close the if statement 
    }

    #endregion

    #region Control Methods

    /// <summary>
    /// Move the player
    /// </summary>
    private void MovePlayer(bool loosedGrounding, bool wasGrounded, Vector3 move)
    {
        // Replace with => JumpButton.Pressed
        // PC -> Input.GetButton("Jump")
        if (m_Grounded && JumpButton.Pressed)
        {
            m_VerticalSpeed = JumpSpeed;
            m_Grounded = false;
            loosedGrounding = true;
            PlayJumpingClips(JumpingClip, 0.8f, 1f);
        }

        /**bool running = Input.GetButton("Run") && m_CharacterData
            .CurrentWeapon.CurrentState == Weapon.WeaponState.Idle;**/
        

        bool running = SprintButton.Pressed && m_CharacterData
            .CurrentWeapon.CurrentState == Weapon.WeaponState.Idle;

        m_IsRunning = running;

        Move(loosedGrounding, move);

        TurnPlayer();

        m_VerticalSpeed = m_VerticalSpeed - 10.0f * Time.deltaTime;
        if (m_VerticalSpeed < -10.0f)
        {
            m_VerticalSpeed = -10.0f;
        }
        var VerticalMove = new Vector3(0, m_VerticalSpeed * Time.deltaTime, 0);
        var flag = m_CharacterController.Move(VerticalMove);
        if ((flag & CollisionFlags.Below) != 0)
        {
            m_VerticalSpeed = 0;
        }

        if (!wasGrounded && m_Grounded)
        {
            PlayJumpingClips(LandingClip, 0.8f, 1f);
        }
    }

    /// <summary>
    /// Computes actual player movement [using keyboard for testing]
    /// ...
    /// </summary>
    private void Move(bool loosedGrounding, Vector3 move)
    {
        actualSpeed = m_IsRunning ? RunningSpeed : PlayerSpeed;

        if (loosedGrounding)
        {
            m_SpeedAtJump = actualSpeed;
        }

        // Prototype movement using keyboard
        move = new Vector3(Joystick.Horizontal, 0, Joystick.Vertical); //To be used with joystick for final build!
        //move = new Vector3(Input.GetAxis("Horizontal"), 0, Input.GetAxisRaw("Vertical"));
        if (move.sqrMagnitude > 1.0f)
        {
            move.Normalize();
        }

        usedSpeed = m_Grounded ? actualSpeed : m_SpeedAtJump;
        move = move * usedSpeed * Time.deltaTime;
        move = transform.TransformDirection(move);
        m_CharacterController.Move(move);
        Speed = move.magnitude / (PlayerSpeed * Time.deltaTime);
    }

    /// <summary>
    /// Turns the player [using mouse input for testing]
    /// </summary>
    private void TurnPlayer()
    {
        //float touchSensitivity;
        //GameManager.instance.GetTouchSensitivity(out touchSensitivity);
        // Prototype with mouse for testing
        turnPlayer = TouchField.TouchDistance.x * touchSensitivity;
        //turnPlayer = Input.GetAxis("Mouse X") * MouseSensitivity;
        m_HorizontalAngle = m_HorizontalAngle + turnPlayer;

        if (m_HorizontalAngle > 360f) m_HorizontalAngle -= 360f;
        if (m_HorizontalAngle < 0) m_HorizontalAngle += 360f;

        currentAngles = transform.localEulerAngles;
        currentAngles.y = m_HorizontalAngle;
        transform.localEulerAngles = currentAngles;

        var turnCam = -TouchField.TouchDistance.y;
        //var turnCam = -Input.GetAxis("Mouse Y");
        turnCam = turnCam * touchSensitivity;
        //turnCam = turnCam * MouseSensitivity;
        m_VerticalAngle = Mathf.Clamp(turnCam + m_VerticalAngle, -89f, 89f);
        currentAngles = CameraPosition.transform.localEulerAngles;
        currentAngles.x = m_VerticalAngle;
        CameraPosition.transform.localEulerAngles = currentAngles;
    }

    /// <summary>
    /// Defines the input to fire or reload the current weapon
    /// The final build will include code for mobile input
    /// ...
    /// </summary>
    private void UseWeapon()
    {
        if (m_CharacterData.CurrentWeapon != null)
        {
            if (Input.GetButton("Reload"))
                m_CharacterData.CurrentWeapon.Reload();

            /**m_CharacterData.CurrentWeapon.triggerDown = FireButton.Pressed;
            m_CharacterData.CurrentWeapon.triggerDown = FireButton2.Pressed;**/
            if (FireButton.Pressed || FireButton2.Pressed)
            {
                m_CharacterData.CurrentWeapon.triggerDown = true;
            }
            else
            {
                m_CharacterData.CurrentWeapon.triggerDown = false;
            }
            //m_CharacterData.CurrentWeapon.triggerDown = Input.GetMouseButton(0);
        }
    }

    /// <summary>
    /// Called when player jumps or lands
    /// </summary>
    /// <param name="clip">Clip to be played</param>
    /// <param name="pitchMin">minimum pitch</param>
    /// <param name="pitchMax">maximum pitch</param> 
    private void PlayJumpingClips(AudioClip clip, float pitchMin,float pitchMax)
    {
        AudioSource.pitch = Random.Range(pitchMin, pitchMax);
        AudioSource.PlayOneShot(clip);
    }

    private void AdjustReticleSize()
    {
        // Prototyping, build values should be TouchField.TouchDistance.x/y
        /**if (Speed > 0.1f || Input.GetAxis("Mouse X") != 0 || Input.GetAxis("Mouse Y") != 0)
        {
            m_CurrentReticleSize = Mathf.Lerp(m_CurrentReticleSize, MaxSize, Time.deltaTime * ReticleSpeed);
        }
        else
        {
            m_CurrentReticleSize = Mathf.Lerp(m_CurrentReticleSize, RestingSize, Time.deltaTime * ReticleSpeed);
        }
        Reticle.sizeDelta = new Vector2(m_CurrentReticleSize, m_CurrentReticleSize);
        **/

        if (Speed > 0.1f || TouchField.TouchDistance.x != 0 || TouchField.TouchDistance.y != 0)
        {
            m_CurrentReticleSize = Mathf.Lerp(m_CurrentReticleSize, MaxSize, Time.deltaTime * ReticleSpeed);
        }
        else
        {
            m_CurrentReticleSize = Mathf.Lerp(m_CurrentReticleSize, RestingSize, Time.deltaTime * ReticleSpeed);
        }
        Reticle.sizeDelta = new Vector2(m_CurrentReticleSize, m_CurrentReticleSize);
    
    }

    #endregion

    #region Scoping Weapon

    private IEnumerator OnScope()
    {
        yield return new WaitForSeconds(0.15f);
        ScopeOverlay.SetActive(true);
        Helpers.RecursiveLayerChange(WeaponPosition, LayerMask.NameToLayer("Scope"));
        MainCamera.fieldOfView = ScopeFOV;
        ZoomSlider.value = 60 - ScopeFOV;
        PlayerSpeed /= 2;
        RunningSpeed /= 2;
        touchSensitivity /= 2.5f;
    }

    private void UnScope()
    {
        ScopeOverlay.SetActive(false);
        Helpers.RecursiveLayerChange(WeaponPosition, LayerMask.NameToLayer("Weapon"));
        MainCamera.fieldOfView = DefaultFOV;
        PlayerSpeed *= 2;
        RunningSpeed *= 2;
        touchSensitivity *= 2.5f;
    }

    /// <summary>
    /// Event fired when zoom slider value changes
    /// Something is wrong
    /// </summary>
    public void UpdateFOV()
    {
        float fov = ZoomSlider.value;
        MainCamera.fieldOfView = 60 - fov;
    }

    /// <summary>
    /// Event triggered by button click
    /// </summary>
    public void ToggleScopeOverlay()
    {
        if (m_CharacterData.CurrentWeapon.weaponScope == Weapon.WeaponScope.Scoped)
        {
            isScoped = !isScoped;
            m_CharacterData.CurrentWeapon.m_Animator.SetBool("Scoped", isScoped);
            if (isScoped)
            {
                StartCoroutine(OnScope());
            }
            else
            {
                UnScope();
            }
        }
    }

    #endregion

}
