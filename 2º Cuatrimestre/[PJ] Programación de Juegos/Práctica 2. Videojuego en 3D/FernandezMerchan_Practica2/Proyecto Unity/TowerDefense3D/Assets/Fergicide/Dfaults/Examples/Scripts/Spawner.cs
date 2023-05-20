using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace Fergicide
{
	public class Spawner : MonoBehaviour
	{
		public GameObject prefab;
		public List<DfaultsConfig> configs;
		public float radius = 10;
		public Vector3 spacing = Vector3.one;
		public int spawns = 10;
		public int configCopies = 10;
		//public int randSeed;

		// Start is called before the first frame update
		void Start()
		{
			// Sanity check.
			if (prefab.GetComponent<DfaultsController>() == null)
			{
				Debug.LogError("Prefab must have a CapsyControl script.");
				enabled = false;
				return;
			}

			SpawnItems();
		}

		private void SpawnItems()
		{
			// Create a dictionary to track spawn positions.
			FlagDic flagDic = new FlagDic();
			Transform thisTransform;

			// Spawn items.
			for (int i = 0; i < spawns; i++)
			{
				GameObject GO = Instantiate(prefab);
				thisTransform = GO.transform;
				DfaultsController CC = GO.GetComponent<DfaultsController>();
				CC.dfaultsConfig = Instantiate(configs[Random.Range(0, configs.Count)]);
				CC.dfaultsConfig.seed = Random.value;
				Vector3 pos = GetRandomPos3D(flagDic, false);
				thisTransform.position = pos;
				thisTransform.rotation = 
					Quaternion.AngleAxis(Random.value * 359.999f, thisTransform.up);
			}
		}

		private Vector3 GetRandomPos3D(FlagDic dic, bool zeroY)
		{
			bool loop = true;
			Vector3 result = Vector3.zero;
			int maxLoopCount = 0, maxLoops = 100;


			while (loop)
			{
				maxLoopCount++; if (maxLoopCount > maxLoops)
					throw new System.Exception("Maxloops exceeded!");

				result = Random.insideUnitSphere * radius;
				result = new Vector3(
					RoundToNearestMultiple(result.x, spacing.x),
					RoundToNearestMultiple(result.y, spacing.y),
					RoundToNearestMultiple(result.z, spacing.z)
				);

				// If flag doesn't exit in dic.
				if (!dic.GetFlag((int)result.x, (int)result.y, (int)result.z))
				{
					// Add it to dic.
					dic.Add((int)result.x, (int)result.y, (int)result.z);

					loop = false;
				}
			}

			return result;
		}

		private float RoundToNearestMultiple(float value, float multiple)
		{
			float sign = (value < 0) ? -1 : 1; // Sign.
			value = (value < 0) ? -value : value; // Abs.
			float rem = value % multiple; // rem is always >= 0.
			float result = value - rem;
			if (rem > (multiple / 2))
				result += multiple;
			return result * sign;
		}
	}
}