/* This file contains the WebApiConfig class.
* This static class is responsible to setup the configuration for the webservice.
*
* Datei: WebApiConfig.cs Autor: Ramandeep Singh
* Datum: 23.12.2015 Version: 1.0
*/

using System.Web.Http;
using System.Web.Http.Dispatcher;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Webserver
{
    /// <summary>
    ///     This class uses the <see cref="HttpConfiguration" /> to setup the webservice.
    /// </summary>
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services
            config.Formatters.JsonFormatter.SerializerSettings.NullValueHandling = NullValueHandling.Ignore;
            config.Formatters.JsonFormatter.SerializerSettings.Converters.Add(new IsoDateTimeConverter());
            config.Services.Replace(typeof (IHttpControllerSelector), new ExtendedControllerSelector(config));

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute("DefaultApi", "api/{controller}/{id}", new {id = RouteParameter.Optional});
        }
    }
}