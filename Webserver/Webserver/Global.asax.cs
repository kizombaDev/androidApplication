/* This file contains the WebApiApplication class.
* This class is the heart of the application and starts the whole webservice.
*
* Datei: Global.asax.cs Autor: Ramandeep Singh
* Datum: 23.12.2015 Version: 1.0
*/

using System.Web;
using System.Web.Http;

namespace Webserver
{
    public class WebApiApplication : HttpApplication
    {
        protected void Application_Start()
        {
            GlobalConfiguration.Configure(WebApiConfig.Register);
        }
    }
}