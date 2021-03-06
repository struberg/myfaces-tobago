/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This is a "fill-in".
 * If a browser doesn't support "console", or some of its methods, it will be defined.
 * So you don't have to worry about browser support of the calls.
 * This is NOT a wrapper, so stacktraces, and line numbers remain valid.
 *
 * You can use, surely the following methods:
 *
 * console.log()
 *
 * console.debug() // deprecated, use console.log() instead
 *
 * console.info()
 *
 * console.warn()
 *
 * console.error()
 *
 * console.assert()
 *
 * console.time()
 *
 * console.timeEnd()
 *
 * console.count()
 *
 * console.dir()
 *
 * console.exception()
 *
 * console.group() // not implemented
 *
 * console.groupCollapsed() // not implemented
 *
 * console.groupEnd() // not implemented
 *
 * console.profile() // not implemented
 *
 * console.profileEnd() // not implemented
 *
 * console.trace()
 *
 * Hint: Should be used for development only!
 *
 * Restriction: multi args: printf is not implemented for console.log()
 */

if (!window.console) {
  console = {};
}

if (!console.log) {
  console.log = function (message, other) {
    var div = jQuery(".tobago-console");
    if (! console.isConsoleHidden()) {
      div.show();
    }
    var body = div.children(":last");
    var parameters = Array.prototype.slice.call(arguments).join(", ");
    parameters.substr(0, parameters.length - 2);

    // todo: make encoding for all array elements...
    parameters = parameters.replace(/&/g, "&amp;");
    parameters = parameters.replace(/</g, "&lt;");
    parameters = parameters.replace(/>/g, "&gt;");
    parameters = parameters.replace(/"/g, "&quot;");
    jQuery("<p>").appendTo(body).html("<pre>" + parameters + "</pre>");
  };

  jQuery(document).ready(function () {
    var div = jQuery("<div>").appendTo("body");
    div.addClass("tobago-console");
    div.css({
      border: "2px solid red",
      padding: "10px",
      position: "absolute",
      left: "50px",
      top: "50px",
      width: "500px",
      height: "200px",
      backgroundColor: "#ffffff",
      filter: "alpha(opacity=70)",
      opacity: 0.7,
      overflow: "auto"
    });
    div.hide();
    var header = jQuery("<div>").appendTo(div);
    header.css({
//      border: "1px solid red",
      padding: "0 15px 5px 0"
    });
    var body = jQuery("<div>").appendTo(div);
    var title = jQuery("<span>simple console replacement</span>").appendTo(header);
    title.css({fontWeight: "bolder"});
    var close = jQuery("<button>").appendTo(header);
    close.attr("type", "button");
    close.append("Hide");
    close.click(function (event) {
      event.preventDefault();
      event.stopPropagation();
      div.hide();
    });
    var closeForSession = jQuery("<button>").appendTo(header);
    closeForSession.attr("type", "button");
    closeForSession.append("Hide for this Session");
    closeForSession.click(function (event) {
      event.preventDefault();
      event.stopPropagation();
      div.hide();
      console.hideConsoleForThisSession();
    });
    var clear = jQuery("<button>").appendTo(header);
    clear.attr("type", "button");
    clear.append("Clear");
    clear.click(function (event) {
      event.preventDefault();
      event.stopPropagation();
      body.children("p").detach();
    });
  });
}

console.util_array_slice = function (args, drop) {
  var result = [];
  for (var i = drop; i < args.length; i++) {
    result[i - drop] = args[i];
  }
  return result;
};

console.util_stack_trace = function () {
  var result;
  var e = new Error();
  var textarea = jQuery("textarea");
  textarea.val("Start Logging...");
  if (e.stack) { // Firefox && WebKit
    textarea.val(textarea.val() + "\n\n" + e.stack);
    result = e.stack.split('\n');
  } else {
    // poor implementation, but might not better possible
    var currentFunction = arguments.callee.caller;
    result = [];
    var i = 0;
    while (currentFunction) {
      result[i++] = currentFunction.toString();
      currentFunction = currentFunction.caller;
    }
  }

  textarea.val(textarea.val() + "\n\n" + result);

  return result;
};

if (!console.debug) {
  console.debug = function (message, other) {
    console.log("DEBUG: " + message, console.util_array_slice(arguments, 1));
  };
}

if (!console.info) {
  console.info = function (message, other) {
    console.log("INFO: " + message, console.util_array_slice(arguments, 1));
  };
}

if (!console.warn) {
  console.warn = function (message, other) {
    console.log("WARN: " + message, console.util_array_slice(arguments, 1));
  };
}

if (!console.error) {
  console.error = function (message, other) {
    console.log("ERROR: " + message, console.util_array_slice(arguments, 1));
  };
}

if (!console.assert) {
  console.assert = function (test, message, other) { // multiargs?
    if (test == false) {
      console.log("ASSERTION FAILED: " + message, console.util_array_slice(arguments, 2));
    }
  };
}

if (!console.time) {
  console.time = function (name) {
    if (!console.timerMap) {
      console.timerMap = {};
    }
    console.timerMap[name] = new Date().getTime();
  };
}

if (!console.timeEnd) {
  console.timeEnd = function (name) {
    if (!console.timerMap) {
      console.timerMap = {};
    }
    var start = console.timerMap[name];
    if (start) {
      console.info("Timer '" + name + "': " + (new Date().getTime() - start) + " ms");
      console.timerMap[name] = null;
    } else {
      console.warn("Timer '" + name + "' not found!");
    }
  };
}

if (!console.dir) {
  console.dir = function (name) {
    // todo
    console.debug("(dir() not implemented) " + name);
  };
}

if (!console.count) {
  console.count = function (name) {
    if (!console.counterMap) {
      console.counterMap = {};
    }
    if (typeof console.counterMap[name] == "number") {
      console.counterMap[name] = console.counterMap[name] + 1;
    } else {
      console.counterMap[name] = 1;
    }
    console.info(name  + ": " + console.counterMap[name]);
  };
}

if (!console.exception) {
  console.exception = function (other) {
    console.error(arguments);
  };
}

if (!console.group) {
  console.group = function (other) {
    // todo
    console.warn("(group() not implemented)");
  };
}

if (!console.groupCollapsed) {
  console.groupCollapsed = function (other) {
    // todo
    console.warn("(groupCollapsed() not implemented)");
  };
}

if (!console.groupEnd) {
  console.groupEnd = function (other) {
    // todo
    console.warn("(groupEnd() not implemented)");
  };
}

if (!console.profile) {
  console.profile = function (other) {
    console.warn("(profile() not implemented)");
  };
}

if (!console.profileEnd) {
  console.profileEnd = function (other) {
    console.warn("(profileEnd() not implemented)");
  };
}

if (!console.trace) {
  console.trace = function () {
    console.log("STACK TRACE: " + console.util_stack_trace());
  };
}

console.hideConsoleForThisSession = function() {
  document.cookie = "tobagoHideConsole";
};

console.isConsoleHidden = function () {
  var part = document.cookie.split(';');
  for (var i = 0; i < part.length; i++) {
    if ("tobagoHideConsole" == part[i]) {
      return true;
    }
  }
  return false;
};
