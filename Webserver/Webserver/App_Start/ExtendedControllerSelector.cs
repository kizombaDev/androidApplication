/* This file contains the ExtendedControllerSelector class.
* This class is responsible to change the default controller selection behaviour of the web api.
* Usally hyphens are not allowed in controller names (because controller names are also class names).
*
* Datei: ExtendedControllerSelect.cs Autor: Ramandeep Singh
* Datum: 23.12.2015 Version: 1.0
*/

using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Dispatcher;

namespace Webserver
{
    /// <summary>
    ///     Overrides the bevhaviour of the <see cref="DefaultHttpControllerSelector" />.
    /// </summary>
    public class ExtendedControllerSelector : DefaultHttpControllerSelector
    {
        public ExtendedControllerSelector(HttpConfiguration configuration) : base(configuration)
        {
        }

        /// <summary>
        ///     Provides the controller name which is extracted from the URI of the request message.
        ///     If the URI contains a hyphen it will be removed.
        /// </summary>
        /// <param name="request">The current request message.</param>
        /// <returns>The controller to select for this request.</returns>
        public override string GetControllerName(HttpRequestMessage request)
        {
            var controllerName = base.GetControllerName(request);
            return controllerName.Replace("-", string.Empty);
        }
    }
}