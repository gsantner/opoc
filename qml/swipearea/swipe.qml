/* This code was written by Sergejs Kovrovs and has been placed in the public domain. */

import QtQuick 2.0


Item {
    id: root
    width: 480
    height: 320

    property var itemData: ["#22eeeeee", "#22bbbbbb", "#22888888", "#22555555", "#22222222"]
    property int currentIndex: 0

    onCurrentIndexChanged: {
        slide_anim.to = - root.width * currentIndex
        slide_anim.start()
    }

    PropertyAnimation {
        id: slide_anim
        target: content
        easing.type: Easing.OutExpo
        properties: "x"
    }

    Image {
        id: img
        anchors.verticalCenter: root.verticalCenter
        source: "http://www.picgifs.com/wallpapers/wallpapers/abstract-3d/wallpaper_abstract-3d_animaatjes-39.jpg"
        fillMode: Image.PreserveAspectCrop
    }

    Item {
        id: content
        width: root.width * itemData.length
        property double k: (content.width - root.width) / (img.width - root.width)
        onXChanged: {
            img.x = x / k
        }
        Repeater {
            model: itemData.length
            Rectangle {
                x: root.width * index
                width: root.width; height: root.height
                color: itemData[index]
                Text { text: index+1; anchors.centerIn: parent; font.pointSize: 100; color: "#88000000" }
            }
        }
    }

    SwipeArea {
        id: mouse
        anchors.fill: parent
        swipeTreshold: 45
        onMove: {
            content.x = (-root.width * currentIndex) + x
        }
        onSwipe: {
            switch (direction) {
            case dirUp:
            case dirLeft:
                if (currentIndex === itemData.length - 1) {
                    currentIndexChanged()
                }
                else {
                    currentIndex++
                }
                break
            case dirDown:
            case dirRight:
                if (currentIndex === 0) {
                    currentIndexChanged()
                }
                else {
                    currentIndex--
                }
                break
            }
        }
        onCanceled: {
            currentIndexChanged()
        }
    }

    Row {
        anchors { bottom: parent.bottom; bottomMargin: 16; horizontalCenter: parent.horizontalCenter }
        spacing: 16
        Repeater {
            model: itemData.length
            Rectangle {
                width: 12; height: 12; radius: 6
                color: currentIndex === index ? "#88ffffff" : "#88000000"
                border { width: 2; color: currentIndex === index ? "#33000000" : "#11000000" }
            }
        }
    }
}
