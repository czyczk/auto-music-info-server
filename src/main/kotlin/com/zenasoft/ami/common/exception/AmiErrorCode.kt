package com.zenasoft.ami.common.exception

enum class AmiErrorCode(val messageTemplate: String) {

    DEFAULT("Unknown error"),

    /*
     * Controller
     */
    AMI_C001_001("Failed to parse request body: %s"),
    AMI_C001_002("The specified resource is not available: %s"),

    /*
     * Integration
     */

    // AMI_I001: Google search integration
    AMI_I001_001("Failed to fetch Google search result: %s"),

    // AMI_I002: Perplexity API integration
    AMI_I002_001("Failed to get completion from Perplexity API: %s"),

    /*
     * Internal service
     */

    // AMI_S001: Google search service
    AMI_S001_001("Failed to fetch Google search result: %s"),

    // AMI_S002: Perplexity API service
    AMI_S002_001("Failed to fetch Perplexity API result: %s"),
    AMI_S002_002("Failed to interpret the response from Perplexity API as a valid result: %s")

}