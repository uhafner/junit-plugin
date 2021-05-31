/* global jQuery3, bootstrap5, view, echartsJenkinsApi */
(function ($) {
    const trendConfigurationDialogId = 'trend-configuration-default';

    $('#' + trendConfigurationDialogId).on('hidden.bs.modal', function () {
        redrawTrendCharts();
    });

    redrawTrendCharts();
    storeAndRestoreCarousel('trend-carousel');

    /**
     * Activate tooltips.
     */
    $(function () {
        $('[data-bs-toggle="tooltip"]').each(function () {
            const tooltip = new bootstrap5.Tooltip($(this)[0]);
            tooltip.enable();
        });
    });

    /**
     * Redraws the trend charts. Reads the last selected X-Axis type from the browser local storage and
     * redraws the trend charts.
     */
    function redrawTrendCharts() {
        const configuration = JSON.stringify(echartsJenkinsApi.readConfiguration(trendDefaultStorageId));

        /**
         * Creates a build trend chart that shows the number of issues per tool.
         * Requires that a DOM <div> element exists with the ID '#tools-trend-chart'.
         */
        view.getTestDurationTrend(configuration, function (lineModel) {
            echartsJenkinsApi.renderConfigurableZoomableTrendChart('test-duration-trend-chart', lineModel.responseJSON, trendConfigurationDialogId);
        });

        /**
         * Creates a build trend chart that shows the number of issues for a couple of builds.
         * Requires that a DOM <div> element exists with the ID '#severities-trend-chart'.
         */
        view.getTestResultTrend(configuration, function (lineModel) {
            echartsJenkinsApi.renderConfigurableZoomableTrendChart('test-result-trend-chart', lineModel.responseJSON, trendConfigurationDialogId);
        });
    }

    /**
     * Store and restore the selected carousel image in browser's local storage.
     * Additionally, the trend chart is redrawn.
     *
     * @param {String} carouselId - ID of the carousel
     */
    function storeAndRestoreCarousel (carouselId) {
        const carousel = $('#' + carouselId);
        carousel.on('slid.bs.carousel', function (e) {
            localStorage.setItem(carouselId, e.to);
            const chart = $(e.relatedTarget).find('>:first-child')[0].echart;
            if (chart) {
                chart.resize();
            }
        });
        const activeCarousel = localStorage.getItem(carouselId);
        if (activeCarousel) {
            const carouselControl = new bootstrap5.Carousel(carousel[0]);
            carouselControl.to(parseInt(activeCarousel));
            carouselControl.pause();
        }
    }
})(jQuery3);
