/*
Author: Andrew Ferguson, fergicide@gmail.com

This is a dictionary is useful for tracking free slots in a 2D or 3D grid,
for example for placing only one item in each location.

We only store "true" values in the dictionary.  If a key can't be found, the slot is free
and then added to the dictionary.  Where a slot is occupied then switched to false, it
gets removed from the dictionary.

*/

using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace Fergicide
{
	public class FlagDic : HashSet<ulong>
	{
		// Alias for SetFlag(x, y, true).
		public void Add(int x, int y)
		{
			SetFlag(x, y, true);
		}

		// Alias for SetFlag(x, y, false).
		public void Remove(int x, int y)
		{
			SetFlag(x, y, false);
		}

		// Alias for SetFlag(x, y, z, true).
		public void Add(int x, int y, int z)
		{
			SetFlag(x, y, z, true);
		}

		// Alias for SetFlag(x, y, z, false).
		public void Remove(int x, int y, int z)
		{
			SetFlag(x, y, z, false);
		}

		// Alias for SetFlag(x, y, z, w, true).
		public void Add(short x, short y, short z, short w)
		{
			SetFlag(x, y, z, w, true);
		}

		// Alias for SetFlag(x, y, z, w, false).
		public void Remove(short x, short y, short z, short w)
		{
			SetFlag(x, y, z, w, false);
		}

		// If setting true, add an entry if one doesn't already exist in list.
		// If setting false, remove the entry if it already exists in list.
		public void SetFlag(int x, int y, bool flag)
		{
			ulong keyHash = GetKeyHash(x, y);
			bool containsKey = Contains(keyHash);

			if (flag && !containsKey) base.Add(keyHash);
			else if (!flag && containsKey) base.Remove(keyHash);
		}

		public void SetFlag(int x, int y, int z, bool flag)
		{
			ulong keyHash = GetKeyHash(x, y, z);
			bool containsKey = Contains(keyHash);

			if (flag && !containsKey) base.Add(keyHash);
			else if (!flag && containsKey) base.Remove(keyHash);
		}

		public void SetFlag(short x, short y, short z, short w, bool flag)
		{
			ulong keyHash = GetKeyHash(x, y, z, w);
			bool containsKey = Contains(keyHash);

			if (flag && !containsKey) base.Add(keyHash);
			else if (!flag && containsKey) base.Remove(keyHash);
		}

		public bool GetFlag(int x, int y)
		{
			return Contains(GetKeyHash(x, y));
		}

		public bool GetFlag(int x, int y, int z)
		{
			return Contains(GetKeyHash(x, y, z));
		}

		public bool GetFlag(short x, short y, short z, short w)
		{
			return Contains(GetKeyHash(x, y, z, w));
		}

		public ulong GetKeyHash(int x, int y)
		{
			return (ulong)(uint)x << 32 | (ulong)(uint)y;
		}

		public ulong GetKeyHash(int x, int y, int z)
		{
			return IntToUlongFrom21Bits(x) << 42 | IntToUlongFrom21Bits(y) << 21 | IntToUlongFrom21Bits(z);
		}

		public ulong GetKeyHash(short x, short y, short z, short w)
		{
			return (ulong)(ushort)x << 48 | (ulong)(ushort)y << 32 | (ulong)(ushort)z << 16 | (ulong)(ushort)w;
		}

		private ulong IntToUlongFrom21Bits(int value)
		{
			int absValue = (value < 0) ? -value : value; // Abs.
			
			// Keep bit count to 20 bits max.
			if (absValue > 1048575) throw new System.Exception("Value must be between -1048575 and 1048575");

			BitArray32 bits = new BitArray32((uint)absValue); // Create bitarray of the absolute value (first 20 bits).
			if (Mathf.Sign(value) == 1) bits.SetBit(20); // Set bit 21 as sign.
			return bits.Bits;
		}
	}
}
