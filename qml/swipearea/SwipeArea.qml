// Initial version was written by Sergejs Kovrovs and has been placed in the public domain.
//   at https://gist.github.com/kovrov/1742405
// Modified by Gregor Santner
import QtQuick 2.0

MouseArea {
    // ############################
    // #   Public properties
    // ############################
    property int swipeTreshold: 32
    property bool ready: false

    readonly property string dirUp:     "up"
    readonly property string dirDown:   "down"
    readonly property string dirLeft:   "left"
    readonly property string dirRight:  "right"

    // ############################
    // #   Signals
    // ############################
    signal move(int x, int y)
    signal swipe(string direction, int distance)


    // ############################
    // #   Item functions
    // ############################
    property point _origin

    onPressed: {
        drag.axis = Drag.XAndYAxis
        _origin = Qt.point(mouse.x, mouse.y)
    }

    onPositionChanged: {
        switch (drag.axis) {
        case Drag.XAndYAxis:
            if (Math.abs(mouse.x - _origin.x) > swipeTreshold)
                drag.axis = Drag.XAxis
            else if (Math.abs(mouse.y - _origin.y) > swipeTreshold)
                drag.axis = Drag.YAxis
            break

        case Drag.XAxis: move(mouse.x - _origin.x, 0) ; break
        case Drag.YAxis: move(0, mouse.y - _origin.y) ; break
        }
    }

    onReleased: {
        switch (drag.axis) {
        case Drag.XAndYAxis: canceled(mouse) ; break
        case Drag.XAxis: swipe(mouse.x - _origin.x < 0 ? dirLeft : dirRight, Math.abs(mouse.x-_origin.x)) ; break
        case Drag.YAxis: swipe(mouse.y - _origin.y < 0 ? dirUp : dirDown, Math.abs(mouse.y-_origin.y)) ; break
        }
    }
}