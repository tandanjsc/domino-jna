package com.mindoo.domino.jna.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.mindoo.domino.jna.NotesCollection;
import com.mindoo.domino.jna.NotesCollectionStats;
import com.mindoo.domino.jna.NotesIDTable;
import com.mindoo.domino.jna.NotesItem;
import com.mindoo.domino.jna.NotesTimeDate;
import com.mindoo.domino.jna.NotesViewEntryData;
import com.mindoo.domino.jna.NotesViewLookupResultData;
import com.mindoo.domino.jna.constants.ReadMask;
import com.mindoo.domino.jna.internal.structs.NotesCollectionPositionStruct;
import com.mindoo.domino.jna.internal.structs.NotesCollectionStatsStruct;
import com.mindoo.domino.jna.internal.structs.NotesItemTableStruct;
import com.mindoo.domino.jna.utils.LMBCSString;
import com.mindoo.domino.jna.utils.NotesDateTimeUtils;
import com.mindoo.domino.jna.utils.NotesNamingUtils;
import com.mindoo.domino.jna.utils.PlatformUtils;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

/**
 * Utility class to decode the buffer returned by data lookups, e.g. in {@link NotesCollection}'s
 * and for database searches.
 * 
 * @author Karsten Lehmann
 */
public class NotesLookupResultBufferDecoder {
	
	/**
	 * Decodes the buffer, 32 bit mode
	 * 
	 * @param parentCollection parent collection
	 * @param bufferHandle buffer handle
	 * @param numEntriesSkipped entries skipped during collection scan
	 * @param numEntriesReturned entries read during collection scan
	 * @param returnMask bitmask used to fill the buffer with data
	 * @param signalFlags signal flags returned by NIFReadEntries, e.g. whether we have more data to read
	 * @param pos position of first match, if returned by find method
	 * @param indexModifiedSequenceNo index modified sequence no
	 * @param retDiffTime only set in {@link NotesCollection#readEntriesExt(com.mindoo.domino.jna.NotesCollectionPosition, EnumSet, int, EnumSet, int, EnumSet, NotesTimeDate, NotesIDTable, Integer)}
	 * @param convertStringsLazily true to delay string conversion until the first use
	 * @param singleColumnLookupName for single column lookups, programmatic name of lookup column
	 * @return collection data
	 */
	public static NotesViewLookupResultData b32_decodeCollectionLookupResultBuffer(NotesCollection parentCollection, int bufferHandle, int numEntriesSkipped, int numEntriesReturned,
			EnumSet<ReadMask> returnMask, short signalFlags, String pos,
			int indexModifiedSequenceNo, NotesTimeDate retDiffTime, boolean convertStringsLazily, String singleColumnLookupName) {
		return b64_decodeCollectionLookupResultBuffer(parentCollection, bufferHandle, numEntriesSkipped, numEntriesReturned,
				returnMask, signalFlags, pos, indexModifiedSequenceNo, retDiffTime, convertStringsLazily, singleColumnLookupName);
	}

	/**
	 * Decodes the buffer, 64 bit mode
	 * 
	 * @param parentCollection parent collection
	 * @param bufferHandle buffer handle
	 * @param numEntriesSkipped entries skipped during collection scan
	 * @param numEntriesReturned entries read during collection scan
	 * @param returnMask bitmask used to fill the buffer with data
	 * @param signalFlags signal flags returned by NIFReadEntries, e.g. whether we have more data to read
	 * @param pos position to add to NotesViewLookupResultData object in case view data is read via {@link NotesCollection#findByKeyExtended2(EnumSet, EnumSet, Object...)}
	 * @param indexModifiedSequenceNo index modified sequence no
	 * @param retDiffTime only set in {@link NotesCollection#readEntriesExt(com.mindoo.domino.jna.NotesCollectionPosition, EnumSet, int, EnumSet, int, EnumSet, NotesTimeDate, NotesIDTable, Integer)}
	 * @param convertStringsLazily true to delay string conversion until the first use
	 * @param singleColumnLookupName for single column lookups, programmatic name of lookup column
	 * @return collection data
	 */
	public static NotesViewLookupResultData b64_decodeCollectionLookupResultBuffer(NotesCollection parentCollection, long bufferHandle, int numEntriesSkipped, int numEntriesReturned,
			EnumSet<ReadMask> returnMask, short signalFlags, String pos, int indexModifiedSequenceNo, NotesTimeDate retDiffTime,
			boolean convertStringsLazily, String singleColumnLookupName) {

		Pointer bufferPtr;
		if (PlatformUtils.is64Bit()) {
			bufferPtr = NotesNativeAPI64.get().OSLockObject(bufferHandle);
		}
		else {
			bufferPtr = NotesNativeAPI32.get().OSLockObject((int) bufferHandle);
		}
		
		int bufferPos = 0;
		
		NotesCollectionStats collectionStats = null;
		
		//compute structure sizes
		
		try {
			if (returnMask.contains(ReadMask.COLLECTIONSTATS)) {
				NotesCollectionStatsStruct tmpStats = NotesCollectionStatsStruct.newInstance(bufferPtr);
				tmpStats.read();
				
				collectionStats = new NotesCollectionStats(tmpStats.TopLevelEntries, tmpStats.LastModifiedTime);
						
				bufferPos += tmpStats.size();
			}

			List<NotesViewEntryData> viewEntries = new ArrayList<NotesViewEntryData>();
			
            int gmtOffset = NotesDateTimeUtils.getGMTOffset();
            boolean useDayLight = NotesDateTimeUtils.isDaylightTime();
            
            Memory sharedCollectionPositionMem = null;
			NotesCollectionPositionStruct sharedPosition = null;
			if (returnMask.contains(ReadMask.INDEXPOSITION)) {
				//allocate memory for a position
				sharedCollectionPositionMem = new Memory(NotesConstants.collectionPositionSize);
				sharedPosition = NotesCollectionPositionStruct.newInstance(sharedCollectionPositionMem);
			}
			
			for (int i=0; i<numEntriesReturned; i++) {
				NotesViewEntryData newData = new NotesViewEntryData(parentCollection);
				viewEntries.add(newData);
				
				if (returnMask.contains(ReadMask.NOTEID)) {
					int entryNoteId = bufferPtr.getInt(bufferPos);
					newData.setNoteId(entryNoteId);
					
					bufferPos+=4;
				}
				
				if (returnMask.contains(ReadMask.NOTEUNID)) {
					long[] unidLongs = bufferPtr.getLongArray(bufferPos, 2);
					newData.setUNID(unidLongs);
					
					bufferPos+=16;
				}
				if (returnMask.contains(ReadMask.NOTECLASS)) {
					short noteClass = bufferPtr.getShort(bufferPos);
					newData.setNoteClass(noteClass);
					
					bufferPos+=2;
				}
				if (returnMask.contains(ReadMask.INDEXSIBLINGS)) {
					int siblingCount = bufferPtr.getInt(bufferPos);
					newData.setSiblingCount(siblingCount);
					
					bufferPos+=4;
				}
				if (returnMask.contains(ReadMask.INDEXCHILDREN)) {
					int childCount = bufferPtr.getInt(bufferPos);
					newData.setChildCount(childCount);
					
					bufferPos+=4;
				}
				if (returnMask.contains(ReadMask.INDEXDESCENDANTS)) {
					int descendantCount = bufferPtr.getInt(bufferPos);
					newData.setDescendantCount(descendantCount);
					
					bufferPos+=4;
				}
				if (returnMask.contains(ReadMask.INDEXANYUNREAD)) {
					boolean isAnyUnread = bufferPtr.getShort(bufferPos) == 1;
					newData.setAnyUnread(isAnyUnread);
					
					bufferPos+=2;
				}
				if (returnMask.contains(ReadMask.INDENTLEVELS)) {
					short indentLevels = bufferPtr.getShort(bufferPos);
					newData.setIndentLevels(indentLevels);
					
					bufferPos += 2;
				}
				if (returnMask.contains(ReadMask.SCORE)) {
					short score = bufferPtr.getShort(bufferPos);
					newData.setFTScore(score);
					
					bufferPos += 2;
				}
				if (returnMask.contains(ReadMask.INDEXUNREAD)) {
					boolean isUnread = bufferPtr.getShort(bufferPos) == 1;
					newData.setUnread(isUnread);
					
					bufferPos+=2;
				}
				if (returnMask.contains(ReadMask.INDEXPOSITION)) {
					short level = bufferPtr.getShort(bufferPos);
					int[] posArr = new int[level+1];
					bufferPtr.read(bufferPos + 2 /* level */  + 2 /* MinLevel+MaxLevel */, posArr, 0, level+1);
							
					newData.setPosition(posArr);
					
					bufferPos += 4 * (level + 2);
				}
				if (returnMask.contains(ReadMask.SUMMARYVALUES)) {
//					The information in a view summary of values is as follows:
//
//						ITEM_VALUE_TABLE containing header information (total length of summary, number of items in summary)
//						WORD containing the length of item #1 (including data type)
//						WORD containing the length of item #2 (including data type)
//						WORD containing the length of item #3 (including data type)
//						...
//						USHORT containing the data type of item #1
//						value of item #1
//						USHORT containing the data type of item #2
//						value of item #2
//						USHORT containing the data type of item #3
//						value of item #3
//						....
					
					int startBufferPosOfSummaryValues = bufferPos;

					Pointer itemValueTablePtr = bufferPtr.share(bufferPos);
					ItemValueTableData itemTableData = decodeItemValueTable(itemValueTablePtr, gmtOffset, useDayLight, convertStringsLazily);
					
					//move to the end of the buffer
					bufferPos = startBufferPosOfSummaryValues + itemTableData.getTotalBufferLength();

					Object[] decodedItemValues = itemTableData.getItemValues();
					newData.setColumnValues(decodedItemValues);
					//add some statistical information to the data object to be able to see which columns "pollute" the summary buffer
					newData.setColumnValueSizesInBytes(itemTableData.getItemValueLengthsInBytes());
				}
				if (returnMask.contains(ReadMask.SUMMARY)) {
					int startBufferPosOfSummaryValues = bufferPos;

					Pointer itemTablePtr = bufferPtr.share(bufferPos);
					ItemTableData itemTableData = decodeItemTable(itemTablePtr, gmtOffset, useDayLight, convertStringsLazily);
					
					//move to the end of the buffer
					bufferPos = startBufferPosOfSummaryValues + itemTableData.getTotalBufferLength();

					Map<String,Object> itemValues = itemTableData.asMap(false);
					newData.setSummaryData(itemValues);
				}
				if (singleColumnLookupName!=null) {
					newData.setSingleColumnLookupName(singleColumnLookupName);
				}
			}
			
			return new NotesViewLookupResultData(collectionStats, viewEntries, numEntriesSkipped, numEntriesReturned, signalFlags, pos, indexModifiedSequenceNo, retDiffTime);
		}
		finally {
			if (PlatformUtils.is64Bit()) {
				NotesNativeAPI64.get().OSUnlockObject(bufferHandle);
				NotesNativeAPI64.get().OSMemFree(bufferHandle);
			}
			else {
				NotesNativeAPI32.get().OSUnlockObject((int)bufferHandle);
				NotesNativeAPI32.get().OSMemFree((int)bufferHandle);
			}
		}
		
	}

	/**
	 * Decodes an ITEM_VALUE_TABLE structure, which contains an ordered list of item values
	 * 
	 * @param bufferPtr pointer to a buffer
	 * @param gmtOffset GMT offset ({@link NotesDateTimeUtils#getGMTOffset()}) to parse datetime values
	 * @param useDayLight DST ({@link NotesDateTimeUtils#isDaylightTime()}) to parse datetime values
	 * @param convertStringsLazily true to delay string conversion until the first use
	 * @return item value table data
	 */
	public static ItemValueTableData decodeItemValueTable(Pointer bufferPtr, int gmtOffset, boolean useDayLight, boolean convertStringsLazily) {
		int bufferPos = 0;
		
		//skip item value table header
		bufferPos += NotesConstants.itemValueTableSize;
		
//		The information in a view summary of values is as follows:
//
//			ITEM_VALUE_TABLE containing header information (total length of summary, number of items in summary)
//			WORD containing the length of item #1 (including data type)
//			WORD containing the length of item #2 (including data type)
//			WORD containing the length of item #3 (including data type)
//			...
//			USHORT containing the data type of item #1
//			value of item #1
//			USHORT containing the data type of item #2
//			value of item #2
//			USHORT containing the data type of item #3
//			value of item #3
//			....
		
		int totalBufferLength = bufferPtr.getShort(0) & 0xffff;
		int itemsCount = bufferPtr.getShort(2) & 0xffff;
		
		int[] itemValueLengths = new int[itemsCount];
		//we don't have any item names:
		int[] itemNameLengths = null;
		
		//read all item lengths
		for (int j=0; j<itemsCount; j++) {
			//convert USHORT to int without sign
			itemValueLengths[j] = bufferPtr.getShort(bufferPos) & 0xffff;
			bufferPos += 2;
		}

		ItemValueTableData data = new ItemValueTableData();
		data.m_totalBufferLength = totalBufferLength;
		data.m_itemsCount = itemsCount;

		Pointer itemValuePtr = bufferPtr.share(bufferPos);
		populateItemValueTableData(itemValuePtr, gmtOffset, useDayLight, itemsCount, itemNameLengths, itemValueLengths, data, convertStringsLazily);

		return data;
	}

	/**
	 * This utility method extracts the item values from the buffer
	 * 
	 * @param bufferPtr buffer pointer
	 * @param gmtOffset GMT offset ({@link NotesDateTimeUtils#getGMTOffset()}) to parse datetime values
	 * @param useDayLight DST ({@link NotesDateTimeUtils#isDaylightTime()}) to parse datetime values
	 * @param itemsCount number of items in the buffer
	 * @param itemValueLengths lengths of the item values
	 * @param retData data object to populate
	 * @param convertStringsLazily true to delay string conversion until the first use
	 */
	private static void populateItemValueTableData(Pointer bufferPtr, int gmtOffset, boolean useDayLight, int itemsCount, int[] itemNameLengths, int[] itemValueLengths, ItemValueTableData retData, boolean convertStringsLazily) {
		int bufferPos = 0;
		String[] itemNames = new String[itemsCount];
		int[] itemDataTypes = new int[itemsCount];
		Pointer[] itemValueBufferPointers = new Pointer[itemsCount];
		int[] itemValueBufferSizes = new int[itemsCount];
		Object[] decodedItemValues = new Object[itemsCount];
		
		for (int j=0; j<itemsCount; j++) {
			if (itemNameLengths!=null && itemNameLengths[j]>0) {
				//casting to short should be safe for item names
				byte[] stringDataArr = new byte[itemNameLengths[j]];
				bufferPtr.read(bufferPos, stringDataArr, 0, itemNameLengths[j]);
				
				LMBCSString lmbcsString = new LMBCSString(stringDataArr);
				itemNames[j] = lmbcsString.getValue();
				bufferPos += itemNameLengths[j];
			}
			
			
			//read data type
			if (itemValueLengths[j] == 0) {
				/* If an item has zero length it indicates an "empty" item in the
				summary. This might occur in a lower-level category and stand for a
				higher-level category that has already appeared. Or an empty item might
				be a field that is missing in a response doc. Just print * as a place
				holder and go on to the next item in the pSummary. */
				continue;
			}
			else {
				itemDataTypes[j] = bufferPtr.getShort(bufferPos) & 0xffff;
				
				//add data type size to position
				bufferPos += 2;
				
				//read item values
				itemValueBufferPointers[j] = bufferPtr.share(bufferPos);
				itemValueBufferSizes[j] = itemValueLengths[j] - 2;
				
				//skip item value
				bufferPos += (itemValueLengths[j] - 2);

				if (itemDataTypes[j] == NotesItem.TYPE_TEXT) {
					Object strVal = ItemDecoder.decodeTextValue(itemValueBufferPointers[j], (int) (itemValueBufferSizes[j] & 0xffff), convertStringsLazily);
					decodedItemValues[j] = strVal;
				}
				else if (itemDataTypes[j] == NotesItem.TYPE_TEXT_LIST) {
					//read a text list item value
					int valueLength = (int) (itemValueBufferSizes[j] & 0xffff);
					List<Object> listValues = valueLength==0 ? Collections.emptyList() : ItemDecoder.decodeTextListValue(itemValueBufferPointers[j], convertStringsLazily);
					decodedItemValues[j]  = listValues;
				}
				else if (itemDataTypes[j] == NotesItem.TYPE_NUMBER) {
					double numVal = ItemDecoder.decodeNumber(itemValueBufferPointers[j], (int) (itemValueBufferSizes[j] & 0xffff));
					decodedItemValues[j] = numVal;
				}
				else if (itemDataTypes[j] == NotesItem.TYPE_TIME) {
					Calendar cal = ItemDecoder.decodeTimeDate(itemValueBufferPointers[j], (int) (itemValueBufferSizes[j] & 0xffff), useDayLight, gmtOffset);
					decodedItemValues[j]  = cal;
				}
				else if (itemDataTypes[j] == NotesItem.TYPE_NUMBER_RANGE) {
					List<Object> numberList = ItemDecoder.decodeNumberList(itemValueBufferPointers[j], (int) (itemValueBufferSizes[j] & 0xffff));
					decodedItemValues[j]  = numberList;
				}
				else if (itemDataTypes[j] == NotesItem.TYPE_TIME_RANGE) {
					List<Object> calendarValues = ItemDecoder.decodeTimeDateList(itemValueBufferPointers[j], useDayLight, gmtOffset);
					decodedItemValues[j] = calendarValues;
				}
			}
		}
		
		retData.m_itemValues = decodedItemValues;
		retData.m_itemDataTypes = itemDataTypes;
		retData.m_itemValueLengthsInBytes = itemValueLengths;
		
		if (retData instanceof ItemTableData) {
			((ItemTableData)retData).m_itemNames = itemNames;
		}
	}

	/**
	 * Decodes an ITEM_TABLE structure with item names and item values
	 * 
	 * @param bufferPtr pointer to a buffer
	 * @param gmtOffset GMT offset ({@link NotesDateTimeUtils#getGMTOffset()}) to parse datetime values
	 * @param useDayLight DST ({@link NotesDateTimeUtils#isDaylightTime()}) to parse datetime values
	 * @param convertStringsLazily true to delay string conversion until the first use
	 * @return data
	 */
	public static ItemTableData decodeItemTable(Pointer bufferPtr, int gmtOffset, boolean useDayLight, boolean convertStringsLazily) {
		int bufferPos = 0;
		NotesItemTableStruct itemTable = NotesItemTableStruct.newInstance(bufferPtr);
		itemTable.read();
		
		//skip item table header
		bufferPos += itemTable.size();

//		typedef struct {
//			   USHORT Length; /*  total length of this buffer */
//			   USHORT Items;  /* number of items in the table */
//			/* now come an array of ITEMs */
//			/* now comes the packed text containing the item names. */
//			} ITEM_TABLE;					
		
		int itemsCount = itemTable.getItemsAsInt();
		int[] itemValueLengths = new int[itemsCount];
		int[] itemNameLengths = new int[itemsCount];
		
		//read ITEM structures for each item
		for (int j=0; j<itemsCount; j++) {
			Pointer itemPtr = bufferPtr.share(bufferPos);
			itemNameLengths[j] = (int) (itemPtr.getShort(0) & 0xffff);
			itemValueLengths[j] = (int) (itemPtr.getShort(2) & 0xffff);
			
			bufferPos += NotesConstants.tableItemSize;
		}
		
		ItemTableData data = new ItemTableData();
		data.m_totalBufferLength = itemTable.getLengthAsInt();
		data.m_itemsCount = itemsCount;
		
		Pointer itemValuePtr = bufferPtr.share(bufferPos);
		populateItemValueTableData(itemValuePtr, gmtOffset, useDayLight, itemsCount, itemNameLengths, itemValueLengths, data, convertStringsLazily);
		
		return data;
	}

	/**
	 * Container class for the data parsed from an ITEM_VALUE_TABLE structure
	 * 
	 * @author Karsten Lehmann
	 */
	public static class ItemValueTableData {
		protected Object[] m_itemValues;
		protected int[] m_itemDataTypes;
		protected int m_totalBufferLength;
		protected int m_itemsCount;
		protected int[] m_itemValueLengthsInBytes;
		
		/**
		 * Returns the decoded item values, with the following types:<br>
		 * <ul>
		 * <li>{@link NotesItem#TYPE_TEXT} - {@link String}</li>
		 * <li>{@link NotesItem#TYPE_TEXT_LIST} - {@link List} of {@link String}</li>
		 * <li>{@link NotesItem#TYPE_NUMBER} - {@link Double}</li>
		 * <li>{@link NotesItem#TYPE_NUMBER_RANGE} - {@link List} with {@link Double} values for number lists or double[] values for number ranges (not sure if Notes views really supports them)</li>
		 * <li>{@link NotesItem#TYPE_TIME} - {@link Calendar}</li>
		 * <li>{@link NotesItem#TYPE_TIME_RANGE} - {@link List} with {@link Calendar} values for number lists or Calendar[] values for datetime ranges</li>
		 * </ul>
		 * 
		 * @return values
		 */
		public Object[] getItemValues() {
			return m_itemValues;
		}
		
		/**
		 * Returns the data types of decoded item values, e.g. {@link NotesItem#TYPE_TEXT},
		 * {@link NotesItem#TYPE_TEXT_LIST}, {@link NotesItem#TYPE_NUMBER},
		 * {@link NotesItem#TYPE_NUMBER_RANGE}
		 * 
		 * @return data type
		 */
		public int[] getItemDataTypes() {
			return m_itemDataTypes;
		}
		
		/**
		 * Returns the total length of the summary buffer
		 * 
		 * @return length
		 */
		public int getTotalBufferLength() {
			return m_totalBufferLength;
		}
		
		/**
		 * Returns the number of decoded items
		 * 
		 * @return number
		 */
		public int getItemsCount() {
			return m_itemsCount;
		}
		
		/**
		 * Returns the lengths of the encoded item values in bytes, e.g. for of each column
		 * in a collection (for {@link ReadMask#SUMMARYVALUES}) or for the summary buffer items
		 * returned for {@link ReadMask#SUMMARY}.
		 * 
		 * @return lengths
		 */
		public int[] getItemValueLengthsInBytes() {
			return m_itemValueLengthsInBytes;
		}
	}
	
	/**
	 * Container class for the data parsed from an ITEM_VALUE structure
	 * 
	 * @author Karsten Lehmann
	 */
	public static class ItemTableData extends ItemValueTableData {
		protected String[] m_itemNames;
		
		/**
		 * Returns the names of the decoded items (programmatic column names in case of collection data)
		 * 
		 * @return names
		 */
		public String[] getItemNames() {
			return m_itemNames;
		}
		
		/**
		 * Returns a single value by its programmatic column name
		 * 
		 * @param itemName item name, case insensitive
		 * @return value or null
		 */
		public Object get(String itemName) {
			for (int i=0; i<m_itemNames.length; i++) {
				if (m_itemNames[i].equalsIgnoreCase(itemName)) {
					Object val = m_itemValues[i];
					if (val instanceof LMBCSString) {
						return ((LMBCSString)val).getValue();
					}
					else if (val instanceof List) {
						List<Object> valAsList = (List<Object>) val;
						for (int j=0; j<valAsList.size(); j++) {
							Object currListValue = valAsList.get(j);
							
							if (currListValue instanceof LMBCSString) {
								valAsList.set(j, ((LMBCSString)currListValue).getValue());
							}
						}
						return valAsList;
					}
					else {
						return val;
					}
				}
			}
			return null;
		}
		
		/**
		 * Convenience function that converts a summary value to a string
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if value is empty or is not a string
		 * @return string value or null
		 */
		public String getAsString(String itemName, String defaultValue) {
			Object val = get(itemName);
			if (val instanceof String) {
				return (String) val;
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				if (valAsList.isEmpty()) {
					return defaultValue;
				}
				else {
					return valAsList.get(0).toString();
				}
			}
			return defaultValue;
		}

		/**
		 * Convenience function that converts a summary value to an abbreviated name
		 * 
		 * @param itemName item name, case insensitive
		 * @return name or null
		 */
		public String getAsNameAbbreviated(String itemName) {
			String nameStr = getAsString(itemName, null);
			return nameStr==null ? null : NotesNamingUtils.toAbbreviatedName(nameStr);
		}
		
		/**
		 * Convenience function that converts a summary value to a list of abbreviated names
		 * 
		 * @param itemName item name, case insensitive
		 * @return names or null
		 */
		public List<String> getAsNamesListAbbreviated(String itemName) {
			List<String> strList = getAsStringList(itemName, null);
			if (strList!=null) {
				List<String> namesAbbr = new ArrayList<String>(strList.size());
				for (int i=0; i<strList.size(); i++) {
					namesAbbr.add(NotesNamingUtils.toAbbreviatedName(strList.get(i)));
				}
				return namesAbbr;
			}
			else
				return null;
		}
		
		/**
		 * Convenience function that converts a summary value to a string list
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if column is empty or is not a number
		 * @return string list value or null
		 */
		public List<String> getAsStringList(String itemName, List<String> defaultValue) {
			Object val = get(itemName);
			if (val instanceof String) {
				return Arrays.asList((String) val);
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				boolean correctType=true;
				for (int i=0; i<valAsList.size(); i++) {
					if (!(valAsList.get(i) instanceof String)) {
						correctType=false;
						break;
					}
				}
				
				if (correctType) {
					return (List<String>) valAsList;
				}
				else {
					List<String> strList = new ArrayList<String>();
					for (int i=0; i<valAsList.size(); i++) {
						strList.add(valAsList.get(i).toString());
					}
					return strList;
				}
			}
			else if (val!=null) {
				return Arrays.asList(val.toString());
			}
			return defaultValue;
		}
		
		/**
		 * Convenience function that converts a summary value to a {@link Calendar}
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if column is empty or is not a Calendar
		 * @return calendar value or null
		 */
		public Calendar getAsCalendar(String itemName, Calendar defaultValue) {
			Object val = get(itemName);
			if (val instanceof Calendar) {
				return (Calendar) val;
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				if (!valAsList.isEmpty()) {
					Object firstVal = valAsList.get(0);
					if (firstVal instanceof Calendar) {
						return (Calendar) firstVal;
					}
				}
			}
			return defaultValue;
		}

		/**
		 * Convenience function that converts a summary value to a {@link Calendar} list
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if column is empty or is not a number
		 * @return calendar list value or null
		 */
		public List<Calendar> getAsCalendarList(String itemName, List<Calendar> defaultValue) {
			Object val = get(itemName);
			if (val instanceof Calendar) {
				return Arrays.asList((Calendar) val);
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				boolean correctType=true;
				for (int i=0; i<valAsList.size(); i++) {
					if (!(valAsList.get(i) instanceof Calendar)) {
						correctType=false;
						break;
					}
				}
				
				if (correctType) {
					return (List<Calendar>) valAsList;
				}
				else {
					return defaultValue;
				}
			}
			return defaultValue;
		}
		
		/**
		 * Convenience function that converts a summary value to a double
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if column is empty or is not a number
		 * @return double
		 */
		public Double getAsDouble(String itemName, Double defaultValue) {
			Object val = get(itemName);
			if (val instanceof Number) {
				return ((Number) val).doubleValue();
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				if (!valAsList.isEmpty()) {
					Object firstVal = valAsList.get(0);
					if (firstVal instanceof Number) {
						return ((Number) firstVal).doubleValue();
					}
				}
			}
			return defaultValue;
		}

		/**
		 * Convenience function that converts a summary value to a double
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if column is empty or is not a number
		 * @return integer
		 */
		public Integer getAsInteger(String itemName, Integer defaultValue) {
			Object val = get(itemName);
			if (val instanceof Number) {
				return ((Number) val).intValue();
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				if (!valAsList.isEmpty()) {
					Object firstVal = valAsList.get(0);
					if (firstVal instanceof Number) {
						return ((Number) firstVal).intValue();
					}
				}
			}
			return defaultValue;
		}

		/**
		 * Convenience function that converts a summary value to a double list
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if column is empty or is not a number
		 * @return double list
		 */
		public List<Double> getAsDoubleList(String itemName, List<Double> defaultValue) {
			Object val = get(itemName);
			if (val instanceof Number) {
				return Arrays.asList(((Number) val).doubleValue());
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				boolean correctType=true;
				boolean numberList=true;
				
				for (int i=0; i<valAsList.size(); i++) {
					Object currObj = valAsList.get(i);
					
					if (currObj instanceof Double) {
						//ok
					}
					else if (currObj instanceof Number) {
						correctType=false;
						numberList=true;
					}
					else {
						correctType=false;
						numberList=false;
					}
				}
				
				if (correctType) {
					return (List<Double>) valAsList;
				}
				else if (numberList) {
					List<Double> dblList = new ArrayList<Double>(valAsList.size());
					for (int i=0; i<valAsList.size(); i++) {
						dblList.add(((Number)valAsList.get(i)).doubleValue());
					}
					return dblList;
				}
				else {
					return defaultValue;
				}
			}
			return defaultValue;
		}

		/**
		 * Convenience function that converts a summary value to a integer list
		 * 
		 * @param itemName item name, case insensitive
		 * @param defaultValue default value if column is empty or is not a number
		 * @return integer list
		 */
		public List<Integer> getAsIntegerList(String itemName, List<Integer> defaultValue) {
			Object val = get(itemName);
			if (val instanceof Number) {
				return Arrays.asList(((Number) val).intValue());
			}
			else if (val instanceof List) {
				List<?> valAsList = (List<?>) val;
				boolean correctType=true;
				boolean numberList=true;
				
				for (int i=0; i<valAsList.size(); i++) {
					Object currObj = valAsList.get(i);
					
					if (currObj instanceof Integer) {
						//ok
					}
					else if (currObj instanceof Number) {
						correctType=false;
						numberList=true;
					}
					else {
						correctType=false;
						numberList=false;
					}
				}
				
				if (correctType) {
					return (List<Integer>) valAsList;
				}
				else if (numberList) {
					List<Integer> intList = new ArrayList<Integer>(valAsList.size());
					for (int i=0; i<valAsList.size(); i++) {
						intList.add(((Number)valAsList.get(i)).intValue());
					}
					return intList;
				}
				else {
					return defaultValue;
				}
			}
			return defaultValue;
		}

		/**
		 * Converts the values to a Java {@link Map}
		 * 
		 * @return data as map
		 */
		public Map<String,Object> asMap() {
			return asMap(true);
		}
		
		/**
		 * Converts the values to a Java {@link Map}
		 * 
		 * @param decodeLMBCS true to convert {@link LMBCSString} objects and lists to Java Strings
		 * @return data as map
		 */
		public Map<String,Object> asMap(boolean decodeLMBCS) {
			Map<String,Object> data = new CaseInsensitiveMap<String, Object>();
			int itemCount = getItemsCount();
			for (int i=0; i<itemCount; i++) {
				Object val = m_itemValues[i];
				
				if (val instanceof LMBCSString) {
					if (decodeLMBCS) {
						data.put(m_itemNames[i], ((LMBCSString)val).getValue());
					}
					else {
						data.put(m_itemNames[i], val);
					}
				}
				else if (val instanceof List) {
					if (decodeLMBCS) {
						//check for LMBCS strings
						List valAsList = (List) val;
						boolean hasLMBCS = false;
						
						for (int j=0; j<valAsList.size(); j++) {
							if (valAsList.get(j) instanceof LMBCSString) {
								hasLMBCS = true;
								break;
							}
						}
						
						if (hasLMBCS) {
							List<Object> convList = new ArrayList<Object>(valAsList.size());
							for (int j=0; j<valAsList.size(); j++) {
								Object currObj = valAsList.get(j);
								if (currObj instanceof LMBCSString) {
									convList.add(((LMBCSString)currObj).getValue());
								}
								else {
									convList.add(currObj);
								}
							}
							data.put(m_itemNames[i], convList);
						}
						else {
							data.put(m_itemNames[i], val);
						}
					}
					else {
						data.put(m_itemNames[i], val);
					}
				}
				else {
					data.put(m_itemNames[i], val);
				}
			}
			return data;
		}
	}
	
}
