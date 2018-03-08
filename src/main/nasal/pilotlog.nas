################################################################################
#
# PilotLog Client
#
# Copyright (c) 2017 Richard Senior
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 3
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
# MA 02110-1301, USA.
#
################################################################################

var host = "localhost";
var port = "8080";

var base = "http://"~host~":"~port~"/api/";

var root = "pilotlog";
var inhibited = root~"/inhibited";
var flight = root~"/flight";

props.globals.initNode(inhibited, 0, "BOOL");

# Utility functions

var inhibit = func(time = 15)
{
    setprop(inhibited, 1);
    var timer = maketimer(time, func {
        setprop(inhibited, 0);
    });
    timer.singleShot = 1;
    timer.start();
}

var request = func(url) {
    if (!getprop("sim/replay/replay-state")) {
        print("Logbook: ", url);
        fgcommand("xmlhttprequest", props.Node.new(
            {url: url, targetnode: flight}
        ));
    }
}

# Web service requests

var departure = func {
    var url = base;
    url ~= "departure";
    url ~= "?callsign="~getprop("sim/multiplay/callsign");
    url ~= "&aircraft="~getprop("sim/aero");
    url ~= "&airport="~getprop("sim/airport/closest-airport-id");
    url ~= "&altitude="~getprop("position/altitude-ft");
    url ~= "&fuel="~getprop("consumables/fuel/total-fuel-gals");
    url ~= "&odometer="~getprop("instrumentation/gps/odometer");
    url ~= "&latitude="~getprop("position/latitude-deg");
    url ~= "&longitude="~getprop("position/longitude-deg");
    request(url);
}

var arrival = func {
    var id = getprop(flight, "id");
    if (id) {
        var url = base;
        url ~= "arrival";
        url ~= "?id="~id;
        url ~= "&airport="~getprop("sim/airport/closest-airport-id");
        url ~= "&altitude="~getprop("position/altitude-ft");
        url ~= "&fuel="~getprop("consumables/fuel/total-fuel-gals");
        url ~= "&odometer="~getprop("instrumentation/gps/odometer");
        url ~= "&latitude="~getprop("position/latitude-deg");
        url ~= "&longitude="~getprop("position/longitude-deg");
        request(url);
    }
}

var invalidate = func(reason) {
    var id = getprop(flight, "id");
    if (id) {
        var url = base;
        url ~= "invalidate";
        url ~= "?id="~id;
        url ~= "&reason="~reason;
        request(url);
    }
}

var pirep = func {
    var id = getprop(flight, "id");
    if (id) {
        var url = base;
        url ~= "pirep";
        url ~= "?id="~id;
        url ~= "&altitude="~getprop("position/altitude-ft");
        url ~= "&fuel="~getprop("consumables/fuel/total-fuel-gals");
        url ~= "&odometer="~getprop("instrumentation/gps/odometer");
        url ~= "&latitude="~getprop("position/latitude-deg");
        url ~= "&longitude="~getprop("position/longitude-deg");
        request(url);
    }
}

# Listeners and timers

var pirepTimer = maketimer(10, pirep);

setlistener("gear/gear/wow", func(node) {
    if (getprop(inhibited))
        return;
    inhibit();
    if (!node.getBoolValue()) {
        departure();
        pirepTimer.start();
    } else {
        pirepTimer.stop();
        arrival();
    }
}, startup=0, runtime=0);

setlistener("sim/speed-up", func(node) {
    if (int(node.getValue()) != 1)
        invalidate("Simulation+speed+was+changed");
}, startup=0, runtime=0);

setlistener("sim/freeze/master", func(node) {
    if (node.getBoolValue()) {
        # Allow accidental pauses, as long as they are reverted
        # within a short timeframe
        var t = maketimer(5, func {
            if (node.getBoolValue())
                invalidate("Simulator+was+paused");
        });
        t.singleShot = 1;
        t.start();
    }
}, startup=0, runtime=0);

print("PilotLog client loaded");
