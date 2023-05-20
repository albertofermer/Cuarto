using UnityEngine;
using System.Collections;

public class ExtendedFlycam : MonoBehaviour
{

	/*
	EXTENDED FLYCAM
		Desi Quintans (CowfaceGames.com), 17 August 2012.
		Based on FlyThrough.js by Slin (http://wiki.unity3d.com/index.php/FlyThrough), 17 May 2011.
 
	LICENSE
		Free as in speech, and free as in beer.
 
	FEATURES
		WASD/Arrows:    Movement
				  Q:    Climb
				  E:    Drop
					  Shift:    Move faster
					Control:    Move slower
						End:    Toggle cursor locking to screen (you can also press Ctrl+P to toggle play mode on and off).
	*/

	public bool lockCursor = true;
	public float cameraSensitivity = 10;
	public float mouselookSensitivity = 15;
	public float climbSpeed = 10;
	public float normalMoveSpeed = 20;
	public float slowMoveFactor = 0.25f;
	public float fastMoveFactor = 6;

	private float rotationX = 0.0f;
	private float rotationY = 0.0f;
	private Transform thisTransform;
	private float xBuffer, yBuffer;
	private bool pauseMode = false;

	void Awake()
	{
		thisTransform = transform;
	}

	void Start()
	{
		SetCursor(lockCursor);

		// Align with camera initial rotation
		rotationX = thisTransform.rotation.eulerAngles.y;
		rotationY = -thisTransform.rotation.eulerAngles.x;
		if (rotationY > 90)
		{
			rotationY = 360 - rotationY;
		}
		else if (rotationY < -90)
		{
			rotationY = rotationY + 360;
		}
	}

	void Update()
	{
		// mouselook

		if (Input.GetKeyDown(KeyCode.RightControl) && !pauseMode)
		{
			SetCursor(false);
			pauseMode = true;
		}
		else if (Input.GetKeyUp(KeyCode.RightControl) && pauseMode)
		{
			SetCursor(lockCursor);
			pauseMode = false;
		}

		if (pauseMode) return;

		xBuffer = Mathf.Lerp(xBuffer, Input.GetAxis("Mouse X"), mouselookSensitivity * Time.deltaTime);
		yBuffer = Mathf.Lerp(yBuffer, Input.GetAxis("Mouse Y"), mouselookSensitivity * Time.deltaTime);

		rotationX += xBuffer * cameraSensitivity;
		rotationY += yBuffer * cameraSensitivity;
		rotationY = Mathf.Clamp(rotationY, -90, 90);

		thisTransform.localRotation = Quaternion.AngleAxis(rotationX, Vector3.up);
		thisTransform.localRotation *= Quaternion.AngleAxis(rotationY, Vector3.left);

		if (Input.GetKey(KeyCode.LeftControl) || Input.GetKey(KeyCode.RightControl)) // slow movement
		{
			if (Input.GetKey(KeyCode.Q)) { thisTransform.position += thisTransform.up * climbSpeed * slowMoveFactor * Time.deltaTime; } // ascend slow speed
			else if (Input.GetKey(KeyCode.E)) { thisTransform.position -= thisTransform.up * climbSpeed * slowMoveFactor * Time.deltaTime; } // descend slow speed
			else
			{
				thisTransform.position += thisTransform.forward * (normalMoveSpeed * slowMoveFactor) * Input.GetAxis("Vertical") * Time.deltaTime;
				thisTransform.position += thisTransform.right * (normalMoveSpeed * slowMoveFactor) * Input.GetAxis("Horizontal") * Time.deltaTime;
			}
		}
		else if (Input.GetKey(KeyCode.LeftShift) || Input.GetKey(KeyCode.RightShift)) // fast movement
		{
			if (Input.GetKey(KeyCode.Q)) { thisTransform.position += thisTransform.up * climbSpeed * fastMoveFactor * Time.deltaTime; } // ascend fast speed
			else if (Input.GetKey(KeyCode.E)) { thisTransform.position -= thisTransform.up * climbSpeed * fastMoveFactor * Time.deltaTime; } // descend fast speed
			else
			{
				thisTransform.position += thisTransform.forward * (normalMoveSpeed * fastMoveFactor) * Input.GetAxis("Vertical") * Time.deltaTime;
				thisTransform.position += thisTransform.right * (normalMoveSpeed * fastMoveFactor) * Input.GetAxis("Horizontal") * Time.deltaTime;
			}
		}

		else if (Input.GetKey(KeyCode.Q)) { thisTransform.position += thisTransform.up * climbSpeed * Time.deltaTime; } // ascend normal speed
		else if (Input.GetKey(KeyCode.E)) { thisTransform.position -= thisTransform.up * climbSpeed * Time.deltaTime; } // descend normal speed
		else // normal movement
		{
			thisTransform.position += thisTransform.forward * normalMoveSpeed * Input.GetAxis("Vertical") * Time.deltaTime;
			thisTransform.position += thisTransform.right * normalMoveSpeed * Input.GetAxis("Horizontal") * Time.deltaTime;
		}
	}

	void SetCursor(bool locked)
	{
		Cursor.lockState = locked ? CursorLockMode.Locked : CursorLockMode.None;
		Cursor.visible = !locked;
	}
}