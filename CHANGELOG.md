# Change Log
All notable changes to this project will be documented in this
file. This change log follows the conventions of
[keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## [0.1.8] - 2026-02-10
### Changed
- Split the specs for the metadata option for the `put-object` method and the return value of the `get-object` method. Because they are very similar, but not identical. The former has the `:content-disposition` key whose value must be keyword for a restricted set, while the later has the `:content-disposition` key whose value is a string, with the full `Content-Disposition` header value for the object.

## [0.1.7] - 2026-02-02
### Changed
- Added :metadata key as a required value (that may be nil) in the map returned by `get-object`, when successfully executed.

## [0.1.6] - 2026-02-02
### Added
- Added new metadata options for `put-object` method: `:content-type`, `:content-disposition` and `:content-encoding` options.

### Changed
- Upgraded Clojure version to 1.12.4 and spec.alpha to 0.5.238
- Upgraded linting dependencies to latest stable versions
- Upgraded github Actions dependencies

## [0.1.5] - 2024-03-18
### Added
- Added specs to get public object-url

## [0.1.4] - 2022-05-23
### Changed
- Moving the repository to [gethop-dev](https://github.com/gethop-dev) organization
- CI/CD solution switch from [TravisCI](https://travis-ci.org/) to [GitHub Actions](Ihttps://github.com/features/actions)
- `cljfmt` and `eastwood` dependencies bump
- update this changelog's releases tags links

### Added
- Source code linting using [clj-kondo](https://github.com/clj-kondo/clj-kondo)

## [0.1.3] - 2021-12-17
### Changed
- Changed specs for `get-object-url-opts`

## [0.1.2] - 2020-05-05
### Added
- Add new `copy-object` method

## [0.1.1] - 2020-02-28
### Added
- Add optional configuration argument to list-objects method

## [0.1.0] - 2019-12-05
### Added
- Initial version

[Unreleased]:  https://github.com/gethop-dev/object-storage.core/compare/v0.1.8...HEAD
[0.1.8]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.8
[0.1.7]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.7
[0.1.6]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.6
[0.1.5]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.5
[0.1.4]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.4
[0.1.3]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.3
[0.1.2]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.2
[0.1.1]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.1
[0.1.0]: https://github.com/gethop-dev/object-storage.core/releases/tag/v0.1.0
