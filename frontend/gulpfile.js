const _ = require('underscore');
const babelify = require('babelify');
const browserify = require('browserify');
const concat = require('gulp-concat');
const gulp = require('gulp');
const less = require('gulp-less');
const source = require('vinyl-source-stream');
const jade = require('jade');
const gulpJade = require('gulp-jade');

const bundles = [
    'app-layout'
];

gulp.task('build', function () {
    _.each(bundles, function (sourceFile) {
        browserify('./src/js/' + sourceFile)
            .transform('babelify', {presets: ['es2015', 'stage-0', 'react']})
            .bundle()
            .pipe(source(sourceFile + '-bundle.js'))
            .pipe(gulp.dest('./build/js'));
    });

    gulp.src('./src/less/**/*.less')
        .pipe(less())
        .pipe(concat('styles-bundle.css'))
        .pipe(gulp.dest('./build/css'));

    gulp.src('./src/*.jade')
        .pipe(gulpJade({
            jade: jade,
            pretty: true
        }))
        .pipe(gulp.dest('./build'));

    gulp.src('./src/img/*')
        .pipe(gulp.dest('./build/img'));
});
