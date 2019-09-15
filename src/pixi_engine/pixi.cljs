(ns pixi-engine.pixi
  (:require
   [oops.core :refer [oget+]]
   [goog.dom :as gdom]
   [pixi]))

(def PIXIApplication (.-Application pixi))
(def Sprite (.-Sprite pixi))

(defn create-application []
  (PIXIApplication.))

(defn add-child! [container sprite]
  (. container addChild sprite))

(defn get-resource [app name]
  (let [resources (.. app -loader -resources)]
    (oget+ resources name)))

(defn create-sprite [resource]
  (let [sprite (Sprite. (.-texture resource))]
    sprite))
