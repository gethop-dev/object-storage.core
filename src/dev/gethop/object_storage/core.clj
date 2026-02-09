;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.object-storage.core
  (:require [clojure.spec.alpha :as s]
            [dev.gethop.object-storage.core.put-object-spec :as-alias put-object-spec]
            [dev.gethop.object-storage.core.get-object-spec :as-alias get-object-spec]))

;; Specs used to validate arguments and return values for
;; implementations of the protocol
(s/def ::object-id (s/or :string string? :uuid uuid?))
(s/def ::encryption map?)
(s/def ::object (s/or :file #(instance? java.io.File %)
                      :input-stream #(instance? java.io.InputStream %)))
(s/def ::success? boolean?)
(s/def ::error-details map?)
(s/def ::url (s/or :string string? :url #(instance? java.net.URL %)))
(s/def ::method #{:create :read :update :delete})
(s/def ::object-public-url? boolean?)
(s/def ::filename string?)
(s/def ::content-type string?)
(s/def ::put-object-spec/content-disposition #{:attachment :inline})
(s/def ::get-object-spec/content-disposition string?)
(s/def ::content-encoding string?)
(s/def ::object-size (s/and integer? #(>= % 0)))
(s/def ::put-object-spec/metadata (s/keys :opt-un [::object-size
                                                   ::content-type
                                                   ::put-object-spec/content-disposition
                                                   ::content-encoding
                                                   ::filename]))
(s/def ::get-object-spec/metadata (s/keys :opt-un [::object-size
                                                   ::content-type
                                                   ::get-object-spec/content-disposition
                                                   ::content-encoding
                                                   ::filename]))
(s/def ::put-object-opts (s/keys :opt-un [::encryption ::put-object-spec/metadata]))
(s/def ::put-object-args (s/cat :config record? :object-id ::object-id :object ::object :opts ::put-object-opts))
(s/def ::put-object-ret (s/keys :req-un [::success?]
                                :opt-un [::error-details]))

(s/def ::copy-object-opts map?)
(s/def ::copy-object-args (s/cat :config record?
                                 :source-object-id ::object-id
                                 :destination-object-id ::object-id
                                 :opts ::copy-object-opts))
(s/def ::copy-object-ret (s/keys :req-un [::success?]
                                 :opt-un [::error-details]))

(s/def ::get-object-opts (s/keys :opt-un [::encryption]))
(s/def ::get-object-args (s/cat :config record? :object-id ::object-id :opts (s/? ::get-object-opts)))
(s/def ::get-object-ret (s/keys :req-un [::success? (or (and ::object (s/nilable ::get-object-spec/metadata)) ::error-details)]))

#_{:clj-kondo/ignore [:missing-docstring]}
(defmulti get-object-url-opts (fn [_] :default))

(defmethod get-object-url-opts :default [{:keys [content-type content-disposition]}]
  (cond
    content-type
    (s/keys :req-un [::filename ::content-type] :opt-un [::content-disposition ::method ::object-public-url?])

    content-disposition
    (s/keys :req-un [::filename ::content-disposition] :opt-un [::content-type ::method ::object-public-url?])

    :else
    (s/keys :opt-un [::method ::filename ::object-public-url?])))

(s/def ::get-object-url-opts (s/multi-spec get-object-url-opts :default))

(s/def ::get-object-url-args (s/cat :config record? :object-id ::object-id :opts (s/? ::get-object-url-opts)))
(s/def ::get-object-url-ret (s/keys :req-un [::success? (or ::object-url ::error-details)]))

(s/def ::delete-object-opts map?)
(s/def ::delete-object-args (s/cat :config record? :object-id ::object-id :opts (s/? ::delete-object-opts)))
(s/def ::delete-object-ret (s/keys :req-un [::success?]
                                   :opt-un [::error-details]))

(s/def ::rename-object-args (s/cat :config record? :object-id ::object-id :new-object-id ::object-id))
(s/def ::rename-object-ret (s/keys :req-un [::success?]
                                   :opt-un [::error-details]))

(s/def ::recursive? boolean?)
(s/def ::list-objects-opts (s/keys :opt-un [::recursive?]))
(s/def ::list-objects-args (s/cat :config record? :parent-object-id ::object-id :opts (s/? ::list-objects-opts)))
(s/def ::list-objects-ret (s/keys :req-un [::success?]
                                  :opt-un [::error-details]))

(defprotocol ObjectStorage
  "Abstraction for managing objects storage"
  (put-object
    [this object-id object]
    [this object-id object opts]
    "Put `object` in the storage system, using `object-id` as the key.
     Use `opts` to specify additional put options.

    `object` can be either a File object or an InputStream. In the
    latter case, if you know the size of the content in the
    InputStream, add the `:metadata` key to the `opts` map. Its value
    should be a map with a key called `:object-size`, with the size as
    its value.

    For storage systems that support it, you can also specify the
    object Content-Type, and the desired Content-Disposition and/or
    Content-Encoding that will be used for `get-object` operations
    that use the object URL directly. For that, you can pass the
    `:content-type`, `:content-disposition` and/or `:content-encoding`
    keys in the `:metadata` map.")
  (copy-object
    [this source-object-id destination-object-id]
    [this source-object-id destination-object-id opts]
    "Copy object identified with `source-object-id` as key into new object identified with `destination-object-id`
     in same assumed storage system.
     For now there is no support to copy objects between different storage systems.
     Use `opts` to specify additional options.")
  (get-object
    [this object-id]
    [this object-id opts]
    "Get the object with key `object-id` from the storage system, using `opts` options")
  (get-object-url
    [this object-id]
    [this object-id opts]
    "Generates a url allowing access to the object without the need to auth oneself.
     Get the object with key `object-id` from the storage system, using `opts` options")
  (delete-object
    [this object-id]
    [this object-id opts]
    "Delete the object `object-id` from the storage system.
     Use `opts` to specify additional delete options.")
  (rename-object
    [this object-id new-object-id]
    "Rename `object-id` to `new-object-id` in the storage system.")
  (list-objects
    [this parent-object-id]
    [this parent-object-id opts]
    "List all child objects of the `parent-object-id` object.
     Use `opts` to specify additional options"))
