#ifndef __CAR_H__
#define __CAR_H__

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <time.h>
#include <string.h>

//#if not _wfopen
//#define _wfopen fopen
//#endif

#ifndef _strcmpi
#define _strcmpi strcasecmp
#endif

// Various constants used by the CAR header:
#define CAR_MAGIC (uint32_t)('CAR0')
#define CAR_CURRENT_VERSION 1

// The .CAR format header:
struct CAR_Header
{
	uint32_t Magic;		// == CAR_MAGIC
	uint32_t Version;		// == CAR_CURRENT_VERSION
	uint32_t HeaderLength; // == sizeof(CARHeader)
	uint32_t Length;		// == sizeof(Encrypted Data After Header)
	unsigned char padding[16];	// Allow for future expansion without changing header size.			
};

// Validate a given header:
inline bool isUsableCARHeader(CAR_Header* header)
{
	if(!header
	 ||(CAR_MAGIC != header->Magic)
	 ||(CAR_CURRENT_VERSION != header->Version)
	 ||(sizeof(CAR_Header) != header->HeaderLength))
		return false;

	return true;
}

// Just a quick check to see if what we have is actually a CAR file:
inline bool isUsableCARFile(FILE* handle, uint32_t* dataSize = NULL)
{
	fseek(handle,-(int)sizeof(CAR_Header),SEEK_END);

	CAR_Header header;
	memset(&header,0,sizeof(header));

	size_t bytesRead = fread(&header,1,sizeof(header),handle);

	if(sizeof(header) != bytesRead)
	{
		return false;
	}

	if(!isUsableCARHeader(&header))
	{
		return false;
	}

	// Return the size of the encrypted data:
	if ( dataSize )
		*dataSize = header.Length;

	return true;
}

// Just a quick check to see if what we have is actually a CAR file:
inline bool isUsableCARFile(const char* filename, uint32_t* dataSize = NULL)
{
	/*if(!dataSize)
	{
		static unsigned long bogusDataSize;
		dataSize=&bogusDataSize;
	}

	*dataSize = 0;*/

	FILE* handle = fopen(filename,"rb");
	if(!handle)
		return false;

	bool result = isUsableCARFile( handle, dataSize );
	fclose( handle );

	return result;
}

// Just a quick check to see if what we have is actually a CAR file:

//inline bool isUsableCARFile(const wchar_t* filename, unsigned long* dataSize = NULL)
//{
//	/*if(!dataSize)
//	{
//		static unsigned long bogusDataSize;
//		dataSize=&bogusDataSize;
//	}
//
//	*dataSize = 0;*/
//
//	FILE* handle = _wfopen(filename,L"rb");
//	if(!handle)
//		return false;
//
//	bool result = isUsableCARFile( handle, dataSize );
//	fclose( handle );
//
//	return result;
//}

// CAR Encryption / Decryption implementation:
#define CAR_OFFSET_SALT (uint32_t)('S41t')

inline unsigned char CAR_EncryptionByteForOffset(uint32_t offset)
{
	uint32_t saltedOffset = CAR_OFFSET_SALT ^ offset;

	// A hack for now:
	return (unsigned char)(((saltedOffset)^(saltedOffset>>8)^(saltedOffset>>16)^(saltedOffset>>24))&0xFF);
}

inline unsigned char CAR_EncryptDecryptByteAtOffset(unsigned char byte, uint32_t offset)
{
	return byte ^ CAR_EncryptionByteForOffset(offset);
}

inline void CAR_EncryptDecryptBufferInPlace(unsigned char* buffer, uint32_t length, uint32_t startOffset=0)
{
	// Validate parameters:
	//if(!buffer || length <=0)
	//	return;

	for(uint32_t index=0;index<length;index++)
		buffer[index] = CAR_EncryptDecryptByteAtOffset(buffer[index],startOffset+index);
}

#endif // __CAR_H__
