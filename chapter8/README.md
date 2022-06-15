# Chapter 8 Exposing EJBs as web services

## Web Services Description Language (WSDL)
You can use the below url in browser :

```shell
> curl http://localhost:8080/chapter8/PlaceBid?wsdl
```
The result would be the below :

```xml
<wsdl:definitions
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:tns="http://ws.actionbazaar.com/"
	xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
	xmlns:ns1="http://schemas.xmlsoap.org/soap/http" name="PlaceBidService" targetNamespace="http://ws.actionbazaar.com/">
	<wsdl:message name="submitBid">
		<wsdl:part name="arg0" type="xsd:long"></wsdl:part>
		<wsdl:part name="arg1" type="xsd:long"></wsdl:part>
		<wsdl:part name="arg2" type="xsd:double"></wsdl:part>
	</wsdl:message>
	<wsdl:message name="submitBidResponse">
		<wsdl:part name="return" type="xsd:long"></wsdl:part>
	</wsdl:message>
	<wsdl:portType name="PlaceBidWS">
		<wsdl:operation name="submitBid">
			<wsdl:input message="tns:submitBid" name="submitBid"></wsdl:input>
			<wsdl:output message="tns:submitBidResponse" name="submitBidResponse"></wsdl:output>
		</wsdl:operation>
	</wsdl:portType>
	<wsdl:binding name="PlaceBidServiceSoapBinding" type="tns:PlaceBidWS">
		<soap:binding style="rpc" transport="http://schemas.xmlsoap.org/soap/http"/>
		<wsdl:operation name="submitBid">
			<soap:operation soapAction="" style="rpc"/>
			<wsdl:input name="submitBid">
				<soap:body namespace="http://ws.actionbazaar.com/" use="literal"/>
			</wsdl:input>
			<wsdl:output name="submitBidResponse">
				<soap:body namespace="http://ws.actionbazaar.com/" use="literal"/>
			</wsdl:output>
		</wsdl:operation>
	</wsdl:binding>
	<wsdl:service name="PlaceBidService">
		<wsdl:port binding="tns:PlaceBidServiceSoapBinding" name="PlaceBidPort">
			<soap:address location="http://localhost:8080/chapter8/PlaceBid"/>
		</wsdl:port>
	</wsdl:service>
</wsdl:definitions>
```
* Tools: https://www.soapui.org/downloads/latest-release/

