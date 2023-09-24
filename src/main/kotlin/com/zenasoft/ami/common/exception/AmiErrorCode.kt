package com.zenasoft.ami.common.exception

enum class AmiErrorCode(val messageTemplate: String) {

    DEFAULT("Unknown error"),

    /*
     * Integration
     */

    // AMI_I001: Google search integration
    AMI_I001_001("Failed to fetch Google search result: %s"),

    /*
     * Internal service
     */

    // AMI_S001: Google search service
    AMI_S001_001("Failed to fetch Google search result: %s"),

}