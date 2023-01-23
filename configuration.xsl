<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <body>
                <h2>Configuration</h2>
                <table border="1">
                    <tr>
                        <th>Connections</th>
                        <th>HTTP</th>
                        <th>Webservice</th>
                        <th>Number of Clients</th>
                    </tr>
                    <tr>
                        <td>
                            <xsl:value-of select="schema/element[1]/complexType/sequence/element[1]/complexType/sequence/element[1]/text()"/>
                        </td>
                        <td>
                            <xsl:value-of select="schema/element[1]/complexType/sequence/element[1]/complexType/sequence/element[2]/text()"/>
                        </td>
                        <td>
                            <xsl:value-of select="schema/element[1]/complexType/sequence/element[1]/complexType/sequence/element[3]/text()"/>
                        </td>
                    </tr>
                </table>
                <br/>
                <table border="1">
                    <tr>
                        <th>Database</th>
                        <th>User</th>
                        <th>Password</th>
                        <th>URL</th>
                    </tr>
                    <tr>
                        <td>
                            <xsl:value-of select="schema/element[1]/complexType/sequence/element[2]/complexType/sequence/element[1]/text()"/>
                        </td>
                        <td>
                            <xsl:value-of select="schema/element[1]/complexType/sequence/element[2]/complexType/sequence/element[2]/text()"/>
                        </td>
                        <td>
                            <xsl:value-of select="schema/element[1]/complexType/sequence/element[2]/complexType/sequence/element[3]/text()"/>
                        </td>
                    </tr>
                </table>
                <br/>
                <h2>Servers</h2>
                <table border="1">
                    <tr>
                        <th>Name</th>
                        <th>WSDL</th>
                        <th>Namespace</th>
                        <th>Service</th>
                        <th>HTTP Address</th>
                    </tr>
                    <xsl:for-each select="schema/element[1]/complexType/sequence/element[3]/complexType/sequence/element[1]/complexType">
                        <tr>
                            <td>
                                <xsl:value-of select="@name"/>
                            </td>
                            <td>
                                <xsl:value-of select="@wsdl"/>
                            </td>
                            <td>
                                <xsl:value-of select="@namespace"/>
                            </td>
                            <td>
                                <xsl:value-of select="@service"/>
                            </td>
                            <td>
                                <xsl:value-of select="@httpAddress"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>