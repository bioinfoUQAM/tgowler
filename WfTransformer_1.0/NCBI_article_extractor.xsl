<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink" 
    xmlns:mml="http://www.w3.org/1998/Math/MathML"
    xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl">
    
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <xsl:element name="articles">
            <xsl:for-each select="//article">
                <xsl:element name="article">
                    <xsl:attribute name="pmc">
                        <xsl:value-of select=".//front//article-id[@pub-id-type='pmc']"/>
                    </xsl:attribute>
                    <xsl:attribute name="article-title">
                        <xsl:value-of select=".//front//article-title"/>
                    </xsl:attribute>
                    <!-- <xsl:attribute name="abstract">
                        <xsl:value-of select=".//front//abstract"/>
                    </xsl:attribute> -->
                    <xsl:copy-of select=".//sec[title[text()[ contains(.,'Phylogenetic Analysis') or contains(.,'Phylogenetic analysis') or contains(.,'phylogenetic analyses') or contains(.,'Phylogenetic Analyses') or contains(.,'Phylogenetic analyses') or contains(.,'phylogenetic analyses') ]]]"/>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>