'use strict';
exports.handler = (event, context, callback) => {
    let success = 0; // Number of valid entries found
    let failure = 0; // Number of invalid entries found
    let dropped = 0; // Number of dropped entries
    const output = event.records.map((record) => {

        const entry = (new Buffer(record.data, 'base64')).toString('utf8');
        let parsed_match = JSON.parse(entry);
        if (parsed_match.type == 1) {
            const result = `${parsed_match.Summons_Number},${parsed_match.Plate_ID},${parsed_match.Issue_Date},${parsed_match.Violation_Code}, ${parsed_match.Vehicle_Make}, ${parsed_match.Street_Name}, ${parsed_match.Violation_County}, ${parsed_match.Violation_Time}` + "\n";
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