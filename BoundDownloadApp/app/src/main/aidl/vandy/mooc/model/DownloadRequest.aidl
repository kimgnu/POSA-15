package vandy.mooc.model;

import vandy.mooc.model.DownloadResults;

interface DownloadRequest {
    oneway void downloadImage(in Uri uri,
                              in DownloadResults callback);
}