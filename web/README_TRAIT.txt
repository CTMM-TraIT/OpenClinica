--------------------------------------------------------------
OPENCLINICA TRAIT RELEASE NOTES
OpenClinica Version ${project.version}
TraIT Version ${trait.version}
--------------------------------------------------------------


--------------------------------------------------------------
Updated: ${changeSetDate}

--------------------------------------------------------------

--------------------------------------------------------------
OVERVIEW
--------------------------------------------------------------

This is OpenClinica release ${project.version} which includes specific modifications made
by TraIT. The current TraIT Version is ${trait.version}.


Below is the release history of the TraIT specified changes:

v3.6_TraIT_2
------------------------------
Release date: 01-03-2019

* Change of references to the old CTMM-helpdesk to the new Health-RI helpdesk
* Addition of a configurable message for the TraIT environment OpenClinica is running under;
  configurable in catalina.properties
* Addition of a message in the login screen on account expiery and failed login attempts. Also configurable in
  catalina.properties

vOCDI_2.0.0
-----------
* Contains bugs fixes for the WS changes introduced by TraIT to allow
interaction with OCDI ( https://github.com/CTMM-TraIT/Open-Clinica-Data-Uploader )

Functionality added in previous TraIT releases
----------------------------------------------
* Logging of exports to register the fields exported, by whom
* Addition of the TraIT skin to OpenClinica
* Export of the missing year of birth in SPSS exports
* Fix of in browsing in the Discrepancy Notes




