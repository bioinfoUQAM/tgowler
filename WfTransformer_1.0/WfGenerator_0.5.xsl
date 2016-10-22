<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink" 
    xmlns:mml="http://www.w3.org/1998/Math/MathML"
    xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template name="splitStringToItems">
        <xsl:param name="list" />
        <xsl:param name="delimiter" select="';'"  />
        <xsl:variable name="_delimiter">
            <xsl:choose>
                <xsl:when test="string-length($delimiter)=0">,</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$delimiter"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="newlist">
            <xsl:choose>
                <xsl:when test="contains($list, $_delimiter)">
                    <xsl:value-of select="normalize-space($list)" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat(normalize-space($list), $_delimiter)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="first" select="substring-before($newlist, $_delimiter)" />
        <xsl:variable name="remaining" select="substring-after($newlist, $_delimiter)" />
        <individual>
            <xsl:value-of select="$first" />
        </individual>
        <xsl:if test="$remaining">
            <xsl:call-template name="splitStringToItems">
                <xsl:with-param name="list" select="$remaining" />
                <xsl:with-param name="delimiter" select="$_delimiter" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/">
        <xsl:for-each select="//Workflow">
            
            <xsl:element name="Sequence">
                
                <xsl:variable
                    name="DataType"
                    select="./@list-DataType">
                </xsl:variable>
                
                <xsl:variable
                    name="GeneOntology"
                    select="./@list-GeneOntology">
                </xsl:variable>
                
                <xsl:variable
                    name="DataBase"
                    select="./@list-DataBase">
                </xsl:variable>
                
                <xsl:variable
                    name="DataTypes"
                    select="./@list-DataType">
                </xsl:variable>
                
                <xsl:variable
                    name="DataFormat"
                    select="./@list-DataFormat">
                </xsl:variable>
                
                <xsl:variable
                    name="SourceTaxon"
                    select="./@list-SourceTaxon">
                </xsl:variable>    
                
                <xsl:variable
                    name="DataCollectionProgram"
                    select="./@list-DataCollectionProgram">
                </xsl:variable>
                
                <xsl:variable
                    name="SequenceAlignmentProgram"
                    select="./@list-SequenceAlignmentProgram">
                </xsl:variable>
                
                <xsl:variable
                    name="ModelProgram"
                    select="./@list-ModelSelectionProgram">
                </xsl:variable>
                
                <xsl:variable
                    name="ModelProgramParameters"
                    select="./@list-ModelProgramParameters">
                </xsl:variable>
                
                <xsl:variable
                    name="PhylogeneticInferenceProgram"
                    select="./@list-PhylogeneticInferenceProgram">
                </xsl:variable>
                
                <xsl:variable
                    name="PhylogeneticInferenceMethod"
                    select="./@list-PhylogeneticInferenceMethod">
                </xsl:variable>
                
                <xsl:variable
                    name="HypothesisValidationProgram"
                    select="./@list-HypothesisValidationProgram">
                </xsl:variable>
                
                <xsl:variable
                    name="HypothesisValidationParameters"
                    select="./@list-HypothesisValidationParameters">
                </xsl:variable>
                
                <xsl:variable
                    name="TreeAnalysisProgram"
                    select="./@list-TreeAnalysisProgram">
                </xsl:variable>
                
                <xsl:variable
                    name="TreeVisualizationProgram"
                    select="./@list-TreeVisualizationProgram">
                </xsl:variable>
                
                
                <xsl:variable name="items0">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$DataType" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items1">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$GeneOntology" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items2">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$DataBase" />
                    </xsl:call-template>
                </xsl:variable>
                
                <!--><xsl:variable name="items3">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$DataFormat" />
                    </xsl:call-template>
                </xsl:variable><-->
                
                <xsl:variable name="items4">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$SourceTaxon" />
                    </xsl:call-template>
                </xsl:variable>
                
                
                <xsl:variable name="items5">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$DataCollectionProgram" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items6">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$SequenceAlignmentProgram" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items7">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$ModelProgram" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items8">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$ModelProgramParameters" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items9">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$PhylogeneticInferenceProgram" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items11">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$PhylogeneticInferenceMethod" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items12">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$HypothesisValidationProgram" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items13">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$HypothesisValidationParameters" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items14">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$TreeAnalysisProgram" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="items15">
                    <xsl:call-template name="splitStringToItems">
                        <xsl:with-param name="delimiter" select="';'" />
                        <xsl:with-param name="list" select="$TreeVisualizationProgram" />
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:copy-of select="$items0"/>
                <xsl:copy-of select="$items1"/>
                <xsl:copy-of select="$items2"/>
                <!--><xsl:copy-of select="$items3"/></-->
                <xsl:copy-of select="$items4"/>
                <xsl:copy-of select="$items5"/>
                <xsl:copy-of select="$items6"/>
                <xsl:copy-of select="$items7"/>
                <xsl:copy-of select="$items8"/>
                <xsl:copy-of select="$items9"/>
                <xsl:copy-of select="$items11"/>
                <xsl:copy-of select="$items12"/>
                <xsl:copy-of select="$items13"/>
                <xsl:copy-of select="$items14"/>
                <xsl:copy-of select="$items15"/>
                
               
                
            
            </xsl:element>

        </xsl:for-each>
        
    </xsl:template>
    
</xsl:stylesheet>