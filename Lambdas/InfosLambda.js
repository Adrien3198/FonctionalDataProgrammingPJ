'use strict';
exports.handler = (event, context, callback) => {
    let success = 0; // Number of valid entries found
    let failure = 0; // Number of invalid entries found
    let dropped = 0; // Number of dropped entries

    /* Process the list of records and transform them */
    const output = event.records.map((record) => {

        const entry = (new Buffer(record.data, 'base64')).toString('utf8');
        let parsed_match = JSON.parse(entry);
        if (parsed_match.type == 0) {
            const result = `${parsed_match.id_drone},${parsed_match.battery},${parsed_match.timestamp},${parsed_match.altitude}, ${parsed_match.temperature}, ${parsed_match.speed}, ${parsed_match.Location.lat}, ${parsed_match.Location.long}` + "\n";
            console.log(result);
            const payload = (new Buffer(result, 'utf8')).toString('base64');
            success++;
            return {
                recordId: record.recordId,
                result: 'Ok',
                data: payload,
            };
        } else {
            return {
                recordId: record.recordId,
                result: 'Dropped',
                data: record.data,
            }
        }
    });
    console.log(`Processing completed.  Successful infos records ${output.length}.`);
    callback(null, {records: output});
};