// CAR.cpp : Defines the entry point for the console application.
//


#define _CRT_SECURE_NO_WARNINGS
//#include <windows.h>

#include "car.h"

// Internal Constants:
#define CAR_OLDEST_SUPPORTED_VERSION 1	// Oldest version supported by this CAR utility.


int CARCreate(const char* inputFilename, const char* outputFilename = NULL )
{
	// If no output filename is specified, assume it's the input filename, but convert the extension to .CAR
	if(!outputFilename)
	{
		outputFilename = inputFilename;

		// TODO: Convert extension to .CAR
	}

	if(isUsableCARFile(inputFilename)) 
	{
		fprintf( stderr, "ERROR: %s is already a CAR file!\n", inputFilename);
		return EXIT_FAILURE;
	}


	if(outputFilename != inputFilename)
	{
		fprintf( stderr, "Creating .CAR: %s -> %s ... ", inputFilename, outputFilename );
	}
	else
	{
		// This format keeps things a little neater when using the ANT build script.
		fprintf( stderr, "Creating .CAR: %s ... ", inputFilename );
	}

	FILE* inputFileHandle = fopen(inputFilename, "rb");
	if(!inputFileHandle)
	{
		fprintf( stderr, "\n*** Failed to open input file: %s\n", inputFilename );
		return EXIT_FAILURE;
	}

	// Determine the length of the input file:
	fseek(inputFileHandle,0,SEEK_END);	// Jump to the beginning of the file
	uint32_t inputFileSize = ftell(inputFileHandle); // Get our offset / size
	fseek(inputFileHandle,0,SEEK_SET);	// Skip back to the beginning.

	// Make sure there is data to read, of course:
	if(inputFileSize <= 0)
	{
		fprintf( stderr, "\n*** Input file '%s' is empty!\n", inputFilename );
		return EXIT_FAILURE;
	}

	// Create a buffer for the input file:
	unsigned char *inputFileBuffer = (unsigned char *)malloc(inputFileSize);
	if(!inputFileBuffer)
	{
		fprintf( stderr, "\n*** Failed to allocate %lu bytes for the input file buffer\n", inputFileSize);
		return EXIT_FAILURE;
	}

	// Read in the input file:
	if( inputFileSize != fread(inputFileBuffer,1,inputFileSize,inputFileHandle) )
	{
		fprintf( stderr, "\n*** Failed to read %lu bytes from '%s'\n", inputFileSize, inputFilename);
		return EXIT_FAILURE;
	}

	// Close the input file, we're done with it:
	fclose(inputFileHandle);

	// Encrypt the input buffer in place:
	clock_t startTime = clock();
	CAR_EncryptDecryptBufferInPlace(inputFileBuffer, inputFileSize);
	clock_t endTime = clock();

	double seconds = (endTime-startTime)/(double)CLOCKS_PER_SEC;
	double bytesPerSecond = inputFileSize / seconds;
	fprintf(stderr, "processed %0.1fm in %0.2f seconds (%0.0f m/second).\n", inputFileSize/(1024.0*1024.0), seconds, bytesPerSecond/(1024.0*1024.0) );

	// Create and fill out the output header:
	CAR_Header header;
	memset(&header,0,sizeof(header));
	header.Magic = CAR_MAGIC;
	header.Version = CAR_CURRENT_VERSION;
	header.HeaderLength = sizeof(header);
	header.Length = inputFileSize;

	// Ensure we didn't just generate garbage:
	if(!isUsableCARHeader(&header))
	{
		fprintf( stderr, "\n*** INTERNAL ERROR - Generated invalid CAR_Header.\n" );
		return EXIT_FAILURE;
	}

	// Write the file out: First open it:
	FILE* outputFileHandle = fopen(outputFilename, "wb+");
	if(!outputFileHandle)
	{
		fprintf( stderr, "\n*** Failed to open output file '%s' for writing.\n", outputFilename );
		return EXIT_FAILURE;
	}

	// Write the data out:
	if(inputFileSize!=fwrite(inputFileBuffer,1,inputFileSize,outputFileHandle))
	{
		fprintf( stderr, "\n*** Failed to write CAR data to '%s'.\n", outputFilename );
		return EXIT_FAILURE;
	}

	// Write the header out (yep, it's a tail header):
	if(sizeof(header)!= fwrite(&header,1,sizeof(header),outputFileHandle))
	{
		fprintf( stderr, "\n*** Failed to write CAR header to '%s'.\n", outputFilename );
		return EXIT_FAILURE;
	}

	// Close down and clean up, we're done here:
	fclose(outputFileHandle);
	free(inputFileBuffer);
	
	return EXIT_SUCCESS;
 
}

int CARExtract(const char* inputFilename, const char* outputFilename = NULL )
{
	uint32_t dataSize = 0;

	// If no output filename is specified, assume it's the input filename, but convert the extension to .CAR
	if(!outputFilename)
	{
		outputFilename = inputFilename; 

		// TODO: Convert extension to .CAR
	}

	if(!isUsableCARFile(inputFilename, &dataSize)) 
	{
		fprintf( stderr, "ERROR: %s is not a valid CAR file!\n", inputFilename);
		return EXIT_FAILURE;
	}

	if(outputFilename != inputFilename)
	{
		fprintf( stderr, "Extracting .CAR: %s -> %s\n", inputFilename, outputFilename );
	}
	else
	{
		// This format keeps things a little neater when using the ANT build script.
		fprintf( stderr, "Extracting .CAR: %s\n", inputFilename );
	}

	FILE* inputFileHandle = fopen(inputFilename, "rb");
	if(!inputFileHandle)
	{
		fprintf( stderr, "\n*** Failed to open input file: %s\n", inputFilename );
		return EXIT_FAILURE;
	}

	unsigned char* buffer = (unsigned char *)malloc(dataSize);
	if(dataSize != fread(buffer,1,dataSize,inputFileHandle))
	{
		fprintf( stderr, "\n*** Failed to read %lu bytes from file: %s\n", dataSize, inputFilename);
		return EXIT_FAILURE;
	}

	fclose(inputFileHandle);

	CAR_EncryptDecryptBufferInPlace(buffer, dataSize);

	FILE* outputFileHandle = fopen(outputFilename, "wb+");
	if(!outputFileHandle)
	{
		fprintf( stderr, "\n*** Failed to open output file: %s\n", outputFilename );
		return EXIT_FAILURE;
	}
	
	if(dataSize != fwrite(buffer,1,dataSize,outputFileHandle))
	{
		fclose(outputFileHandle);
		fprintf( stderr, "\n*** Failed to write %lu bytes to file: %s\n", dataSize, outputFilename);
		return EXIT_FAILURE;
	}

	fclose(outputFileHandle);

	free(buffer);

	return EXIT_SUCCESS;
}

void CARDump(const char* inputFilename )
{
	if(!inputFilename)
	{
		fprintf( stderr, "\n*** NULL filename passed to CARDump.\n" );
		return;
	}

	if(!isUsableCARFile(inputFilename))
	{
		fprintf( stderr, "\n*** '%s' doesn't appear to be a valid CAR file.\n", inputFilename );
		return;
	}

	// ...
	// Print some other info here eventually.  For now, it's enough just to know that the header is valid.
	// ...

	fprintf( stderr, "\n'%s' appears to be in order.\n", inputFilename );

}

int main( int argc, char** argv )
{
	// Check argument count

	if( argc == 3 || argc == 4 )
	{
		if(!_strcmpi(argv[1], "create"))
		{			
			if( argc == 3 )
			{
				return CARCreate(argv[2]);
			} else if( argc == 4 )
			{
				return CARCreate(argv[2],argv[3]);
			}
		}
		else if(!_strcmpi(argv[1], "extract"))
		{
			if( argc == 3 )
			{
				return CARExtract(argv[2]);
			}  else if(argc == 4)
			{
				return CARExtract(argv[2],argv[3]);
			}
		}
		else if(!_strcmpi(argv[1], "dump"))
		{
			CARDump(argv[2]);
			return EXIT_FAILURE;
		}
	}

	// Show the usage if we didn't reconize any commands:
	fprintf( stderr, "\n"
		             "CAR archive maintenance utility - Copyright (c) 2009 by Sparkplay Media\n"
					 "Version: %i (Oldest Supported: %i)  [Last Build: " __DATE__ ", " __TIME__ "]\n"
					 "\n"
					 "Usage: CAR.exe [command] <inputFilename> [outputFilename]\n"
		             "\n"
					 "Commands:\n"
					 "\tcreate\t- Creates .CAR file from input filename.\n"
					 "\textract\t- Extracts original file from .CAR file.\n"
					 "\tdump\t- Dumps useful information about the .CAR file.\n",

					CAR_CURRENT_VERSION,
					CAR_OLDEST_SUPPORTED_VERSION

		);

	return EXIT_FAILURE; // Success
}
